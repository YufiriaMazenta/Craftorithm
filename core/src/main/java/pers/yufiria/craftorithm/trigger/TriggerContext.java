package pers.yufiria.craftorithm.trigger;

import crypticlib.BukkitPlayer;
import crypticlib.Invoker;
import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 触发器上下文
 * 包装事件信息，转换为 ScriptContext 供脚本引擎使用
 */
public class TriggerContext {

    private final @NotNull UUID playerId;
    private final @Nullable NamespacedKey recipeKey;
    private final @Nullable RecipeType recipeType;
    private final Map<String, ScriptValue> variables;

    public TriggerContext(@NotNull UUID playerId, @Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType) {
        this(playerId, recipeKey, recipeType, null);
    }

    public TriggerContext(@NotNull Player player, @Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType) {
        this(player.getUniqueId(), recipeKey, recipeType, null);
    }

    public TriggerContext(@NotNull UUID playerId, @NotNull Map<String, ScriptValue> variables) {
        this(playerId, null, null, new HashMap<>(variables));
    }

    public TriggerContext(@NotNull Player player, @NotNull Map<String, ScriptValue> variables) {
        this(player.getUniqueId(), variables);
    }

    public TriggerContext(@NotNull UUID playerId, @Nullable NamespacedKey recipeKey, @Nullable RecipeType recipeType, Map<String, ScriptValue> variables) {
        this.playerId = playerId;
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
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) {
            throw new IllegalStateException("Player " + playerId + " is not online, cannot create ScriptContext");
        }
        Invoker invoker = BukkitPlayer.byPlayer(player);
        ScriptContext ctx = new ScriptContext(invoker);

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

    public @NotNull UUID playerUniqueId() {
        return playerId;
    }

    public @Nullable Player player() {
        return Bukkit.getPlayer(playerId);
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
