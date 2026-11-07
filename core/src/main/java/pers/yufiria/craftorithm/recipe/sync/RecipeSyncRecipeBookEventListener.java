package pers.yufiria.craftorithm.recipe.sync;

import crypticlib.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRecipeBookSettingsChangeEvent;

/**
 * 配方书设置监听
 * <p>
 * PlayerRecipeBookSettingsChangeEvent 在 1.20 ~ 1.20.1 不存在, 单独放在这里, 让这两个版本只跳过本监听器
 */
@EventListener
public enum RecipeSyncRecipeBookEventListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRecipeBookToggle(PlayerRecipeBookSettingsChangeEvent event) {
        if (!event.isOpen()) {
            return;
        }
        RecipeSyncManager.INSTANCE.syncAfterRecipeBookOpen(event.getPlayer());
    }

}
