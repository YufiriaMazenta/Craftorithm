package pers.yufiria.craftorithm.script;

import org.bukkit.entity.Player;
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
    private final Map<String, ScriptValue> variables = new ConcurrentHashMap<>();

    public ScriptContext(@NotNull Player player) {
        this.player = player;
    }

    public @NotNull Player player() {
        return player;
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
