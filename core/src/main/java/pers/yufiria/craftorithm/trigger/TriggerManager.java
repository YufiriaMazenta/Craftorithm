package pers.yufiria.craftorithm.trigger;

import crypticlib.CrypticLibPlugin;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.trigger.event.EventTriggerTypes;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 触发器管理器
 * 负责触发器类型注册、触发器索引维护和触发执行
 * YAML 解析由 {@link TriggerLoader} 负责
 */
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ACTIVE, priority = 3),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, isAsync = true),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum TriggerManager implements LifecycleTask {

    INSTANCE;

    public final File TRIGGER_FOLDER = new File(
        Craftorithm.instance().getDataFolder(), "triggers"
    );

    // typeKey -> TriggerType
    private final Map<String, TriggerType> triggerTypes = new ConcurrentHashMap<>();
    // 冷却管理
    private final TriggerCooldown cooldownManager = TriggerCooldown.INSTANCE;

    /**
     * 触发器索引快照
     * 重载在异步线程构建完整数据，主线程只读，通过 volatile 引用一次性整体发布，避免读到构建中的集合
     */
    private record TriggerSnapshot(
        Map<String, List<Trigger>> triggersByType,
        Map<String, Trigger> triggerById,
        Set<NamespacedKey> hasTriggerRecipeKeys,
        Set<String> matchAllTypeKeys
    ) {
        static final TriggerSnapshot EMPTY = new TriggerSnapshot(Map.of(), Map.of(), Set.of(), Set.of());
    }

    private volatile TriggerSnapshot snapshot = TriggerSnapshot.EMPTY;

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
        //基于剩余触发器重建索引快照，保证各索引一致
        List<Trigger> remaining = snapshot.triggersByType().values().stream()
            .flatMap(List::stream)
            .filter(trigger -> !trigger.typeKey().equals(typeKey))
            .toList();
        snapshot = buildSnapshot(remaining);
    }

    /**
     * 获取已注册的触发器类型
     */
    public @Nullable TriggerType getTriggerType(String typeKey) {
        return triggerTypes.get(typeKey);
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

        cooldownManager.clear();

        List<Trigger> parsedTriggers = TriggerLoader.loadAll(TRIGGER_FOLDER);
        snapshot = buildSnapshot(parsedTriggers);

        long elapsed = System.currentTimeMillis() - startTime;
        BukkitMsgSender.INSTANCE.info("Loaded " + parsedTriggers.size() + " trigger(s) in " + elapsed + "ms");
    }

    /**
     * 由解析结果构建完整索引快照
     * 禁用的触发器不参与任何索引，避免无谓的触发器检查开销
     */
    private TriggerSnapshot buildSnapshot(List<Trigger> parsedTriggers) {
        Map<String, List<Trigger>> newTriggers = new HashMap<>();
        Map<String, Trigger> newTriggerById = new HashMap<>();
        Set<NamespacedKey> newHasTriggerRecipeKeys = new HashSet<>();
        Set<String> newMatchAllTypeKeys = new HashSet<>();

        for (Trigger trigger : parsedTriggers) {
            if (!trigger.isEnabled()) continue;

            List<NamespacedKey> triggerMatchRecipes = trigger.recipes();
            if (triggerMatchRecipes.isEmpty()) {
                //如果该触发器是合成类型，且没有设置配方，那么标记该触发器类型会匹配所有配方
                TriggerType triggerType = getTriggerType(trigger.typeKey());
                if (triggerType instanceof CraftTriggerTypes) {
                    newMatchAllTypeKeys.add(trigger.typeKey());
                }
            } else {
                newHasTriggerRecipeKeys.addAll(triggerMatchRecipes);
            }

            newTriggers.computeIfAbsent(trigger.typeKey(), k -> new ArrayList<>()).add(trigger);
            newTriggerById.put(trigger.id(), trigger);
        }

        //按 priority 排序并固化为不可变列表,发布后不会再被修改
        Map<String, List<Trigger>> sortedTriggers = new HashMap<>();
        newTriggers.forEach((typeKey, list) -> {
            list.sort(Comparator.comparingInt(Trigger::priority));
            sortedTriggers.put(typeKey, List.copyOf(list));
        });

        return new TriggerSnapshot(
            Map.copyOf(sortedTriggers),
            Map.copyOf(newTriggerById),
            Set.copyOf(newHasTriggerRecipeKeys),
            Set.copyOf(newMatchAllTypeKeys)
        );
    }

    // ---- 触发执行 ----

    /**
     * 获取指定类型的所有触发器
     */
    public List<Trigger> getTriggers(TriggerType triggerType) {
        return getTriggers(triggerType.typeKey());
    }

    /**
     * 获取指定类型的所有触发器
     */
    public List<Trigger> getTriggers(String typeKey) {
        return snapshot.triggersByType().getOrDefault(typeKey, Collections.emptyList());
    }

    /**
     * 该类型下是否存在任意触发器（供无配方维度的通用事件预检）
     */
    public boolean hasAnyTrigger(TriggerType triggerType) {
        return !getTriggers(triggerType).isEmpty();
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
        return snapshot.triggerById().get(fullId);
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
        TriggerSnapshot current = snapshot;
        if (current.matchAllTypeKeys().contains(triggerType.typeKey())) {
            //如果这个类型的触发器有一个匹配所有配方的，且配方key不为null，那么无论如何返回true
            return true;
        }

        return current.hasTriggerRecipeKeys().contains(recipeKey);
    }

    // ---- 生命周期 ----

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase phase) {
        switch (phase) {
            case ACTIVE -> {
                TRIGGER_FOLDER.mkdirs();
                // 初始化动态事件注册器
                EventTriggerTypes.INSTANCE.init();
                for (CraftTriggerTypes type : CraftTriggerTypes.values()) {
                    regTriggerType(type);
                }
                reloadTriggers();
            }
            case RELOAD -> {
                reloadTriggers();
            }
            case DISABLE -> {
                snapshot = TriggerSnapshot.EMPTY;
                triggerTypes.clear();
                cooldownManager.clear();
                EventTriggerTypes.INSTANCE.reset();
            }
        }
    }

}
