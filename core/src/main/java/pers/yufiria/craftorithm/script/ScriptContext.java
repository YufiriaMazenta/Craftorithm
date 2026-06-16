package pers.yufiria.craftorithm.script;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 脚本执行上下文
 * 携带脚本执行时需要的所有环境信息
 */
public class ScriptContext {

    private final @NotNull Player player;
    private final @Nullable Recipe recipe;
    private final @Nullable NamespacedKey recipeKey;
    private final Map<String, ScriptValue> variables = new ConcurrentHashMap<>();

    public ScriptContext(@NotNull Player player) {
        this(player, null, null);
    }

    public ScriptContext(@NotNull Player player, @Nullable Recipe recipe, @Nullable NamespacedKey recipeKey) {
        this.player = player;
        this.recipe = recipe;
        this.recipeKey = recipeKey;
    }

    public @NotNull Player player() {
        return player;
    }

    public @Nullable Recipe recipe() {
        return recipe;
    }

    public @Nullable NamespacedKey recipeKey() {
        return recipeKey;
    }

    // ---- 变量存取 ----

    public void setVariable(@NotNull String name, @NotNull ScriptValue value) {
        variables.put(name, value);
    }

    public @Nullable ScriptValue getVariable(@NotNull String name) {
        return variables.get(name);
    }

    public Map<String, ScriptValue> variables() {
        return variables;
    }
}
