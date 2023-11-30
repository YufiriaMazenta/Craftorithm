package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public enum ItemsAdderHandler implements Listener {

    INSTANCE;

    @EventHandler
    public void onItemsAdderLoaded(ItemsAdderLoadDataEvent event) {
        RecipeManager.reloadRecipeManager();
    }

}
