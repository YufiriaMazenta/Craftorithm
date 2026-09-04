package pers.yufiria.craftorithm.trigger;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.script.ScriptEngine;
import crypticlib.script.compile.CompiledScript;
import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.trigger.event.EventTriggerTypes;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 触发器管理器
 * 负责触发器类型注册、YAML加载、事件监听器管理和触发执行
 */
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ACTIVE, priority = 3),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, isAsync = true)
    }
)
public enum TriggerManager implements LifecycleTask {

    INSTANCE;

    public final File TRIGGER_FOLDER = new File(
        Craftorithm.instance().getDataFolder(), "triggers"
    );

    // typeKey -> TriggerType
    private final Map<String, TriggerType> triggerTypes = new ConcurrentHashMap<>();
    // typeKey -> 按 priority 排序的触发器列表
    private final Map<String, List<Trigger>> triggers = new ConcurrentHashMap<>();
    // 触发器ID -> 触发器（用于快速查找）
    private final Map<String, Trigger> triggerById = new ConcurrentHashMap<>();
    // 记录存在触发器的配方，对于不存在触发器的配方，不执行触发器以减少性能开销
    private final Set<NamespacedKey> hasTriggerRecipeKeys = new HashSet<>();
    // 记录这触发器类型是否存在匹配所有配方的触发器，如果存在的话，所有该类型配方都将执行触发器操作
    private final Map<TriggerType, Boolean> triggerTypeMatchAllMap = new ConcurrentHashMap<>();
    // 冷却管理
    private final TriggerCooldown cooldownManager = TriggerCooldown.INSTANCE;

    // ---- 类型注册 ----

    /**
     * 注册触发器类型
     * 外部插件可调用此方法注册自定义触发器类型
     */
    public void regTriggerType(TriggerType type) {
        triggerTypes.put(type.typeKey(), type);
    }

    /**
     * 注销触发器类型
     */
    public void removeTriggerType(String typeKey) {
        triggerTypes.remove(typeKey);
        triggers.remove(typeKey);
        triggerById.values().removeIf(trigger -> trigger.typeKey().equals(typeKey));
    }

    /**
     * 获取已注册的触发器类型
     */
    public @Nullable TriggerType getTriggerType(String typeKey) {
        TriggerType type = triggerTypes.get(typeKey);
        if (type != null) return type;
        return EventTriggerTypes.INSTANCE.getEventType(typeKey);
    }

    /**
     * 获取所有已注册的触发器类型
     */
    public Map<String, TriggerType> triggerTypes() {
        return Collections.unmodifiableMap(triggerTypes);
    }

    // ---- 触发器加载 ----

    /**
     * 从 triggers 文件夹加载所有触发器
     */
    public void reloadTriggers() {
        long startTime = System.currentTimeMillis();

        // 清理旧数据
        triggers.clear();
        triggerById.clear();
        cooldownManager.clear();
        hasTriggerRecipeKeys.clear();
        triggerTypeMatchAllMap.clear();

        if (!TRIGGER_FOLDER.exists()) {
            TRIGGER_FOLDER.mkdirs();
            return;
        }

        List<File> triggerFiles = IOHelper.allYamlFiles(TRIGGER_FOLDER);
        int count = 0;

        for (File file : triggerFiles) {
            count += loadTriggersFromConfigFile(file);
        }

        // 按 priority 排序
        triggers.values().forEach(list ->
            list.sort(Comparator.comparingInt(Trigger::priority))
        );

        long elapsed = System.currentTimeMillis() - startTime;
        BukkitMsgSender.INSTANCE.info("Loaded " + count + " trigger(s) in " + elapsed + "ms");
    }

    private int loadTriggersFromConfigFile(File file) {
        int count = 0;
        String fileName = file.getName();
        // 去掉扩展名作为文件标识
        String fileKey = fileName.contains(".")
            ? fileName.substring(0, fileName.lastIndexOf('.'))
            : fileName;

        BukkitConfigWrapper wrapper = new BukkitConfigWrapper(file);
        YamlConfiguration config = wrapper.config();

        for (String localId : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(localId);
            if (section == null) continue;

            try {
                String fullId = fileKey + ":" + localId;
                Trigger trigger = parseTrigger(fullId, section);

                if (trigger == null) continue;

                List<NamespacedKey> triggerMatchRecipes = trigger.recipes();
                if (triggerMatchRecipes.isEmpty()) {
                    //如果该触发器是合成类型，且没有设置配方，那么标记该触发器类型会匹配所有配方
                    TriggerType triggerType = getTriggerType(trigger.typeKey());
                    if (triggerType instanceof CraftTriggerTypes) {
                        triggerTypeMatchAllMap.put(triggerType, true);
                    }
                } else {
                    this.hasTriggerRecipeKeys.addAll(triggerMatchRecipes);
                }

                if (trigger.enabled()) {
                    triggers.computeIfAbsent(trigger.typeKey(), k -> new ArrayList<>())
                        .add(trigger);
                    triggerById.put(fullId, trigger);
                    BukkitMsgSender.INSTANCE.info(
                        "Loaded trigger '" + localId + "' in " + fileName
                    );
                }
                count ++;
            } catch (Throwable throwable) {
                BukkitMsgSender.INSTANCE.info(
                    "&cFailed to load trigger '" + localId + "' in " + fileName
                );
                throwable.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 解析单个触发器
     *
     * YAML 结构:
     *   type: 'crafting'
     *   recipes: [...]
     *   conditions: [条件脚本]    ← 正向逻辑，成立=放行
     *   actions: [动作脚本]
     */
    private @Nullable Trigger parseTrigger(String fullId, ConfigurationSection section) {
        String typeKey = section.getString("type");
        if (typeKey == null) {
            BukkitMsgSender.INSTANCE.info("&eTrigger '" + fullId + "' missing 'type' field");
            return null;
        }

        if (getTriggerType(typeKey) == null) {
            BukkitMsgSender.INSTANCE.info("&eUnknown trigger type '" + typeKey + "' in " + fullId);
            return null;
        }

        List<NamespacedKey> recipeKeys = section.getStringList("recipes").stream().map(NamespacedKey::fromString).toList();

        // 编译 conditions 脚本
        // 旧格式（列表，默认 && 连接）:
        //   conditions:
        //     - 'level() >= 10'
        //     - 'perm("vip")'
        // 新格式（对象，支持 mode 字段）:
        //   conditions:
        //     mode: 'block'
        //     body:
        //       - 'if level() >= 10'
        //       - '  return true'
        //       - 'endif'
        CompiledScript conditionScript = null;
        ConfigurationSection condSection = section.getConfigurationSection("conditions");
        if (condSection != null) {
            // 新格式：对象模式
            String mode = condSection.getString("mode", "and");
            List<String> condSources = condSection.getStringList("body");
            if (!condSources.isEmpty()) {
                String joined = "script".equals(mode)
                    ? String.join("\n", condSources)
                    : condSources.size() == 1
                      ? condSources.getFirst()
                      : condSources.stream().map(c -> "(" + c + ")").collect(Collectors.joining(" && "));
                conditionScript = ScriptEngine.INSTANCE.compile(fullId + "_cond", joined);
            }
        } else {
            // 旧格式：列表模式（默认 && 连接）
            List<String> condSources = section.getStringList("conditions");
            if (!condSources.isEmpty()) {
                String joined = condSources.size() == 1
                    ? condSources.getFirst()
                    : condSources.stream().map(c -> "(" + c + ")").collect(Collectors.joining(" && "));
                conditionScript = ScriptEngine.INSTANCE.compile(fullId + "_cond", joined);
            }
        }

        // 编译 actions 脚本
        List<String> actSources = section.getStringList("actions");
        String actSource = String.join("\n", actSources);
        CompiledScript actionScript = ScriptEngine.INSTANCE.compile(fullId + "_act", actSource);

        int priority = section.getInt("priority", 0);
        boolean enable;
        if (section.isBoolean("enable")) {
            enable = section.getBoolean("enable", true);
        } else if (section.isBoolean("enabled")) {
            //用于兼容旧版配置
            enable = section.getBoolean("enabled", true);
        } else {
            enable = true;
        }
        long cooldown = (long) (section.getDouble("cooldown", 0) * 1000);
        boolean perPlayer = section.getBoolean("per_player", true);

        return new Trigger(
            fullId, typeKey, recipeKeys, conditionScript, actionScript,
            priority, enable, cooldown, perPlayer
        );
    }

    // ---- 触发执行 ----

    public List<Trigger> getTriggers(TriggerType triggerType) {
        return triggers.get(triggerType.typeKey());
    }

    /**
     * 获取指定类型的所有触发器
     */
    public List<Trigger> getTriggers(String typeKey) {
        return triggers.getOrDefault(typeKey, Collections.emptyList());
    }

    /**
     * 触发 Prepare 阶段：评估条件，条件不通过的数量即为需要拒绝的数量
     * 如果配方的某个触发器正在冷却, 那么最少返回1
     */
    public int firePrepare(TriggerType triggerType, TriggerContext context) {
        int denied = 0;
        for (Trigger trigger : getTriggers(triggerType)) {
            if (!trigger.matches(context.recipeKey())) continue;
            if (cooldownManager.isOnCooldown(trigger, context.playerUniqueId())) {
                denied ++;
                continue;
            }
            if (!trigger.evaluateConditions(context)) {
                denied++;
            }
        }
        return denied;
    }

    /**
     * 触发实际事件阶段：评估条件，通过则执行 actions
     */
    public boolean fire(TriggerType triggerType, TriggerContext context) {
        boolean fired = false;
        for (Trigger trigger : getTriggers(triggerType)) {
            if (!trigger.matches(context.recipeKey())) continue;
            if (cooldownManager.isOnCooldown(trigger, context.playerUniqueId())) continue;
            if (!trigger.evaluateConditions(context)) continue;

            trigger.execute(context);
            cooldownManager.setCooldown(trigger, context.playerUniqueId());
            fired = true;
        }
        return fired;
    }

    /**
     * 通过完整ID获取触发器
     */
    public @Nullable Trigger getTriggerById(String fullId) {
        return triggerById.get(fullId);
    }

    public TriggerCooldown cooldownManager() {
        return cooldownManager;
    }

    /**
     * 获取一个触发器类型下的某个配方是否存在触发器
     * @param triggerType
     * @param recipeKey
     * @return
     */
    public boolean hasTrigger(TriggerType triggerType, NamespacedKey recipeKey) {
        if (recipeKey == null) {
            return false;
        }
        if (triggerTypeMatchAllMap.getOrDefault(triggerType, false)) {
            //如果这个类型的触发器有一个匹配所有配方的，且配方key不为null，那么无论如何返回true
            return true;
        }

        return hasTriggerRecipeKeys.contains(recipeKey);
    }

    // ---- 生命周期 ----

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase phase) {
        if (phase == LifecyclePhase.ACTIVE) {
            TRIGGER_FOLDER.mkdirs();
            // 初始化动态事件注册器
            EventTriggerTypes.INSTANCE.init();
        }
        // 注册内置类型（仅首次）
        if (triggerTypes.isEmpty()) {
            for (CraftTriggerTypes type : CraftTriggerTypes.values()) {
                regTriggerType(type);
            }
        }
        reloadTriggers();
    }

}
