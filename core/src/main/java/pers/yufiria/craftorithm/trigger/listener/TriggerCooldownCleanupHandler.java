package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pers.yufiria.craftorithm.trigger.TriggerManager;

/**
 * 玩家退出时清理其触发器冷却记录，防止冷却表无限增长
 */
@EventListener
public enum TriggerCooldownCleanupHandler implements Listener {

    INSTANCE;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TriggerManager.INSTANCE.cooldownManager().cleanupPlayer(event.getPlayer().getUniqueId());
    }

}
