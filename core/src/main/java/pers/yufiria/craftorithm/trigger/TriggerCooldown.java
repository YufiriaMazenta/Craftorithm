package pers.yufiria.craftorithm.trigger;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 触发器冷却管理
 */
public class TriggerCooldown {

    // key = triggerId + ":" + playerUUID (perPlayer) 或 triggerId (global)
    private final Map<String, Long> cooldownMap = new ConcurrentHashMap<>();

    public boolean isOnCooldown(Trigger trigger, UUID playerUniqueId) {
        if (trigger.cooldownMillis() <= 0) return false;
        String key = buildKey(trigger, playerUniqueId);
        Long expireTime = cooldownMap.get(key);
        return expireTime != null && System.currentTimeMillis() < expireTime;
    }

    public void setCooldown(Trigger trigger, UUID playerUniqueId) {
        if (trigger.cooldownMillis() <= 0) return;
        String key = buildKey(trigger, playerUniqueId);
        cooldownMap.put(key, System.currentTimeMillis() + trigger.cooldownMillis());
    }

    /**
     * 清理过期的冷却记录
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        cooldownMap.entrySet().removeIf(entry -> entry.getValue() < now);
    }

    /**
     * 清理指定玩家的冷却记录
     */
    public void cleanupPlayer(UUID playerUniqueId) {
        String suffix = ":" + playerUniqueId;
        cooldownMap.keySet().removeIf(key -> key.endsWith(suffix));
    }

    public void clear() {
        cooldownMap.clear();
    }

    private String buildKey(Trigger trigger, UUID playerUniqueId) {
        if (trigger.perPlayer()) {
            return trigger.id() + ":" + playerUniqueId;
        }
        return trigger.id();
    }

}
