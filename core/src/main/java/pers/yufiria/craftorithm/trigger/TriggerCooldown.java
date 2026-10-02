package pers.yufiria.craftorithm.trigger;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 触发器冷却管理
 */
public enum TriggerCooldown {

    INSTANCE;

    /**
     * 冷却记录key：triggerId + 玩家（perPlayer）或仅 triggerId（global）
     */
    private record CooldownKey(String triggerId, @Nullable UUID player) {}

    private final Map<CooldownKey, Long> cooldownMap = new ConcurrentHashMap<>();

    public boolean isOnCooldown(Trigger trigger, UUID playerUniqueId) {
        if (trigger.cooldownMillis() <= 0) return false;
        Long expireTime = cooldownMap.get(buildKey(trigger, playerUniqueId));
        return expireTime != null && System.currentTimeMillis() < expireTime;
    }

    public void setCooldown(Trigger trigger, UUID playerUniqueId) {
        if (trigger.cooldownMillis() <= 0) return;
        cooldownMap.put(buildKey(trigger, playerUniqueId), System.currentTimeMillis() + trigger.cooldownMillis());
    }

    /**
     * 清理过期的冷却记录
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        cooldownMap.entrySet().removeIf(entry -> entry.getValue() < now);
    }

    /**
     * 清理指定玩家的冷却记录（全局冷却不清理）
     */
    public void cleanupPlayer(UUID playerUniqueId) {
        cooldownMap.keySet().removeIf(key -> playerUniqueId.equals(key.player()));
    }

    public void clear() {
        cooldownMap.clear();
    }

    private CooldownKey buildKey(Trigger trigger, UUID playerUniqueId) {
        return trigger.perPlayer()
            ? new CooldownKey(trigger.id(), playerUniqueId)
            : new CooldownKey(trigger.id(), null);
    }

}
