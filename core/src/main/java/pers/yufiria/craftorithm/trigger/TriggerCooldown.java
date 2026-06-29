package pers.yufiria.craftorithm.trigger;

import org.bukkit.Bukkit;
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

    public boolean isOnCooldown(Trigger trigger, @Nullable Player player) {
        if (player == null) return false;
        return isOnCooldown(trigger, player.getUniqueId());
    }

    public void setCooldown(Trigger trigger, UUID playerUniqueId) {
        if (trigger.cooldownMillis() <= 0) return;
        String key = buildKey(trigger, playerUniqueId);
        cooldownMap.put(key, System.currentTimeMillis() + trigger.cooldownMillis());
    }

    public void setCooldown(Trigger trigger, @Nullable Player player) {
        if (player == null) return;
        setCooldown(trigger, player.getUniqueId());
    }

    public long getRemainingMillis(Trigger trigger, UUID playerUniqueId) {
        String key = buildKey(trigger, playerUniqueId);
        Long expireTime = cooldownMap.get(key);
        if (expireTime == null) return 0;
        return Math.max(0, expireTime - System.currentTimeMillis());
    }

    /**
     * 清理过期的冷却记录
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        cooldownMap.entrySet().removeIf(entry -> entry.getValue() < now);
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
