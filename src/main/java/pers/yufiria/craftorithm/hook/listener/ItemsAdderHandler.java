package pers.yufiria.craftorithm.hook.listener;

import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.PluginConfigs;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public enum ItemsAdderHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemsAdderLoaded(ItemsAdderLoadDataEvent event) {
        if (!PluginConfigs.RELOAD_WHEN_IA_RELOAD.value())
            return;
        Craftorithm.instance().reloadPlugin();
//        RecipeManager.INSTANCE.reloadRecipeManager();
//        OtherPluginsListenerProxy.INSTANCE.reloadOtherPluginsListener();
    }

}