package pers.yufiria.craftorithm.trigger;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.compile.CompiledScript;

import java.util.List;

/**
 * 触发器数据模型
 *
 * YAML 结构:
 *   type: 'crafting'
 *   recipes: [...]
 *   conditions: [条件脚本]    ← 正向逻辑，成立=放行
 *   actions: [动作脚本]
 *   priority: 0
 *   enabled: true
 *   cooldown: 0
 *   per-player: true
 */
public class Trigger {

    private final String id;
    private final String typeKey;
    private final List<String> recipes;
    private final CompiledScript conditionScript;  // 条件脚本（正向，成立=放行）
    private final CompiledScript actionScript;     // 动作脚本
    private final int priority;
    private final boolean enabled;
    private final long cooldownMillis;
    private final boolean perPlayer;

    public Trigger(
        String id,
        String typeKey,
        List<String> recipes,
        CompiledScript conditionScript,
        CompiledScript actionScript,
        int priority,
        boolean enabled,
        long cooldownMillis,
        boolean perPlayer
    ) {
        this.id = id;
        this.typeKey = typeKey;
        this.recipes = recipes;
        this.conditionScript = conditionScript;
        this.actionScript = actionScript;
        this.priority = priority;
        this.enabled = enabled;
        this.cooldownMillis = cooldownMillis;
        this.perPlayer = perPlayer;
    }

    public String id() { return id; }
    public String typeKey() { return typeKey; }
    public List<String> recipes() { return recipes; }
    public int priority() { return priority; }
    public boolean enabled() { return enabled; }
    public long cooldownMillis() { return cooldownMillis; }
    public boolean perPlayer() { return perPlayer; }

    /**
     * 检查是否匹配
     * recipes 为空时匹配所有（包括非配方事件）
     * recipes 非空时检查 recipeKey 是否在列表中
     */
    public boolean matches(@Nullable NamespacedKey recipeKey) {
        if (recipes.isEmpty()) return true;
        if (recipeKey == null) return false;
        return recipes.contains(recipeKey.toString());
    }

    /**
     * 评估条件（正向：true = 通过）
     * 条件为空时默认通过
     */
    public boolean evaluateConditions(TriggerContext context) {
        if (conditionScript == null) return true;
        return conditionScript.execute(context.toScriptContext()).asBoolean();
    }

    /**
     * 执行动作脚本
     */
    public void execute(TriggerContext context) {
        ScriptContext scriptCtx = context.toScriptContext();
        CURRENT_TRIGGER_CONTEXT.set(context);
        try {
            actionScript.execute(scriptCtx);
        } finally {
            CURRENT_TRIGGER_CONTEXT.remove();
        }
    }

    public static TriggerContext currentTriggerContext() {
        return CURRENT_TRIGGER_CONTEXT.get();
    }

    private static final ThreadLocal<TriggerContext> CURRENT_TRIGGER_CONTEXT = new ThreadLocal<>();

    public CompiledScript conditionScript() {
        return conditionScript;
    }

    public CompiledScript actionScript() {
        return actionScript;
    }
}
