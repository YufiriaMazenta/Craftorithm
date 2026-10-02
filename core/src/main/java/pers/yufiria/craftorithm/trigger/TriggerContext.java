package pers.yufiria.craftorithm.trigger;

import crypticlib.CommonPlayer;
import crypticlib.Invoker;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.script.RootScriptContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 触发器上下文
 * 包装事件信息，转换为 ScriptContext 供脚本引擎使用
 */
public class TriggerContext {

    private final @NotNull UUID invokerId;
    private final @Nullable NamespacedKey recipeKey;
    private final @Nullable RecipeType recipeType;
    private final Map<String, ScriptValue> variables;

    public TriggerContext(@Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType) {
        this(Invoker.CONSOLE_UUID, recipeKey, recipeType);
    }

    public TriggerContext(@NotNull UUID invokerId, @Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType) {
        this(invokerId, recipeKey, recipeType, null);
    }

    public TriggerContext(@Nullable Player player, @Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType) {
        this(player != null ? player.getUniqueId() : Invoker.CONSOLE_UUID, recipeKey, recipeType, null);
    }

    public TriggerContext(@NotNull Map<String, ScriptValue> variables) {
        this(Invoker.CONSOLE_UUID, null, null, new HashMap<>(variables));
    }

    public TriggerContext(@NotNull UUID invokerId, @NotNull Map<String, ScriptValue> variables) {
        this(invokerId, null, null, new HashMap<>(variables));
    }

    public TriggerContext(@Nullable Player player, @NotNull Map<String, ScriptValue> variables) {
        this(player != null ? player.getUniqueId() : Invoker.CONSOLE_UUID, variables);
    }

    public TriggerContext(@Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType, Map<String, ScriptValue> variables) {
        this(Invoker.CONSOLE_UUID, recipeKey, recipeType, variables);
    }

    public TriggerContext(@NotNull UUID invokerId, @Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType, Map<String, ScriptValue> variables) {
        this.invokerId = invokerId;
        this.recipeKey = recipeKey;
        this.recipeType = recipeType;
        this.variables = variables != null ? new HashMap<>(variables) : new HashMap<>();
    }

    public void setVariable(@NotNull String name, @NotNull ScriptValue value) {
        variables.put(name, value);
    }

    /**
     * 转换为脚本引擎的 ScriptContext
     * 将事件变量注入为脚本可访问的变量
     */
    public ScriptContext toScriptContext() {
        Invoker invoker;
        if (!Invoker.CONSOLE_UUID.equals(invokerId)) {
            invoker = CommonPlayer.fromUuid(invokerId).orElse(null);
            if (invoker == null) {
                invoker = Craftorithm.instance().getConsoleInvoker();
            }
        } else {
            invoker = Craftorithm.instance().getConsoleInvoker();
        }
        ScriptContext ctx = new ScriptContext(invoker, RootScriptContext.INSTANCE);

        if (recipeKey != null) {
            ctx.setVariable("recipe", ScriptValue.of(recipeKey.toString()));
        }
        if (recipeType != null) {
            ctx.setVariable("recipe_type", ScriptValue.of(recipeType.typeKey()));
        }
        for (Map.Entry<String, ScriptValue> entry : variables.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        return ctx;
    }

    public @Nullable UUID playerUniqueId() {
        return invokerId;
    }

    public @Nullable NamespacedKey recipeKey() {
        return recipeKey;
    }

    public @Nullable RecipeType recipeType() {
        return recipeType;
    }

    public @NotNull Map<String, ScriptValue> variables() {
        return variables;
    }

}
