package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public enum ItemsAdderHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemsAdderLoaded(ItemsAdderLoadDataEvent event) {
        RecipeManager.INSTANCE.reloadRecipeManager();
        OtherPluginsListenerProxy.INSTANCE.reloadOtherPluginsListener();
    }

}