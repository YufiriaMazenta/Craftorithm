package pers.yufiria.craftorithm.trigger;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.trigger.event.DynamicEventRegistry;
import pers.yufiria.craftorithm.script.ScriptEngine;
import pers.yufiria.craftorithm.script.compile.CompiledScript;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 触发器管理器
 * 负责触发器类型注册、YAML加载、事件监听器管理和触发执行
 */
@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.ACTIVE, priority = 3),
    @TaskRule(lifeCycle = LifeCycle.RELOAD, priority = 3)
})
public enum TriggerManager implements BukkitLifeCycleTask {

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
    // 冷却管理
    private final TriggerCooldown cooldownManager = new TriggerCooldown();

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
        triggerById.entrySet().removeIf(e -> {
            String id = e.getKey();
            // id 格式为 "filename:triggerId"，触发器的 typeKey 存储在 Trigger 对象中
            return e.getValue().typeKey().equals(typeKey);
        });
    }

    /**
     * 获取已注册的触发器类型
     */
    public @Nullable TriggerType getTriggerType(String typeKey) {
        TriggerType type = triggerTypes.get(typeKey);
        if (type != null) return type;
        return DynamicEventRegistry.INSTANCE.getEventType(typeKey);
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
    public void loadTriggers() {
        // 清理旧数据
        triggers.clear();
        triggerById.clear();
        cooldownManager.clear();

        if (!TRIGGER_FOLDER.exists()) {
            TRIGGER_FOLDER.mkdirs();
            return;
        }

        List<File> triggerFiles = IOHelper.allYamlFiles(TRIGGER_FOLDER);
        int count = 0;

        for (File file : triggerFiles) {
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

                    if (trigger.enabled()) {
                        triggers.computeIfAbsent(trigger.typeKey(), k -> new ArrayList<>())
                                .add(trigger);
                        triggerById.put(fullId, trigger);
                        count++;
                        BukkitMsgSender.INSTANCE.info(
                            "Loaded trigger '" + localId + "' in " + fileName
                        );
                    }
                } catch (Exception e) {
                    BukkitMsgSender.INSTANCE.info(
                        "&cFailed to load trigger '" + localId + "' in " + fileName
                    );
                    e.printStackTrace();
                }
            }
        }

        // 按 priority 排序
        triggers.values().forEach(list ->
            list.sort(Comparator.comparingInt(Trigger::priority))
        );

        BukkitMsgSender.INSTANCE.info("Loaded " + count + " trigger(s)");
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

        List<String> recipeKeys = section.getStringList("recipes");

        // 编译 conditions 脚本（正向逻辑，成立=放行）
        List<String> condSources = section.getStringList("conditions");
        CompiledScript conditionScript = null;
        if (!condSources.isEmpty()) {
            // 多个条件用 && 连接
            String joined = condSources.size() == 1
                ? condSources.get(0)
                : condSources.stream().map(c -> "(" + c + ")").collect(Collectors.joining(" && "));
            conditionScript = ScriptEngine.INSTANCE.compile(fullId + "_cond", joined);
        }

        // 编译 actions 脚本
        List<String> actSources = section.getStringList("actions");
        String actSource = String.join("\n", actSources);
        CompiledScript actionScript = ScriptEngine.INSTANCE.compile(fullId + "_act", actSource);

        int priority = section.getInt("priority", 0);
        boolean enabled = section.getBoolean("enabled", true);
        long cooldown = (long) (section.getDouble("cooldown", 0) * 1000);
        boolean perPlayer = section.getBoolean("per-player", true);

        return new Trigger(
            fullId, typeKey, recipeKeys, conditionScript, actionScript,
            priority, enabled, cooldown, perPlayer
        );
    }

    // ---- 触发执行 ----

    /**
     * 获取指定类型的所有触发器
     */
    public List<Trigger> getTriggers(String typeKey) {
        return triggers.getOrDefault(typeKey, Collections.emptyList());
    }

    /**
     * 触发 Prepare 阶段：评估条件，条件不通过的数量即为需要拒绝的数量
     */
    public int firePrepare(String typeKey, TriggerContext context) {
        int denied = 0;
        for (Trigger trigger : getTriggers(typeKey)) {
            if (!trigger.matches(context.recipeKey())) continue;
            if (cooldownManager.isOnCooldown(trigger, context.playerUniqueId())) continue;
            if (!trigger.evaluateConditions(context)) {
                denied++;
            }
        }
        return denied;
    }

    /**
     * 触发实际事件阶段：评估条件，通过则执行 actions
     */
    public boolean fire(String typeKey, TriggerContext context) {
        boolean fired = false;
        for (Trigger trigger : getTriggers(typeKey)) {
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

    // ---- 生命周期 ----

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        if (lifeCycle == LifeCycle.ACTIVE) {
            TRIGGER_FOLDER.mkdirs();
            // 初始化动态事件注册器
            DynamicEventRegistry.INSTANCE.init();
        }
        // 注册内置类型（仅首次）
        if (triggerTypes.isEmpty()) {
            for (BuiltInTriggerTypes type : BuiltInTriggerTypes.values()) {
                regTriggerType(type);
            }
        }
        loadTriggers();
    }

}
