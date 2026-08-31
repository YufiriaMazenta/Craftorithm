package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.listener.EventListener;
import crypticlib.scheduler.CrypticLibRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pers.yufiria.craftorithm.trigger.TriggerCooldown;
import pers.yufiria.craftorithm.trigger.TriggerManager;

/**
 * 玩家退出时清理其触发器冷却记录，防止冷却表无限增长
 */
@EventListener
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ENABLE),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum TriggerCooldownCleanupHandler implements Listener, LifecycleTask {

    INSTANCE;
    private CrypticLibRunnable cooldownCleanupTask;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TriggerManager.INSTANCE.cooldownManager().cleanupPlayer(event.getPlayer().getUniqueId());
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecycle) {
        switch (lifecycle) {
            case ENABLE -> {
                this.cooldownCleanupTask = new CrypticLibRunnable() {
                    @Override
                    public void run() {
                        TriggerCooldown.INSTANCE.cleanup();
                    }
                };
                this.cooldownCleanupTask.asyncTimer(0, 20L);
            }
            case DISABLE -> {
                if (this.cooldownCleanupTask != null) {
                    cooldownCleanupTask.cancel();
                }
            }
        }
    }
}
