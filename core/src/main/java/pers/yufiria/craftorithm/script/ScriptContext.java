package pers.yufiria.craftorithm.script;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 脚本执行上下文
 * 携带脚本执行时需要的所有环境信息
 */
public class ScriptContext {

    private final @NotNull UUID playerUniqueId;
    private final Map<String, ScriptValue> variables = new ConcurrentHashMap<>();

    public ScriptContext(@NotNull UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    public ScriptContext(@NotNull Player player) {
        this(player.getUniqueId());
    }

    public @NotNull UUID playerUniqueId() {
        return playerUniqueId;
    }

    public @Nullable Player player() {
        return Bukkit.getPlayer(playerUniqueId);
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
