package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public enum ItemsAdderHandler implements Listener {

    INSTANCE;

    private boolean isEnable = true;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemsAdderLoaded(ItemsAdderLoadDataEvent event) {
        if (!PluginConfigs.RELOAD_WHEN_IA_RELOAD.value() && !isEnable)
            return;
        RecipeManager.INSTANCE.reloadRecipeManager();
        OtherPluginsListenerProxy.INSTANCE.reloadOtherPluginsListener();
        isEnable = false;
    }

}