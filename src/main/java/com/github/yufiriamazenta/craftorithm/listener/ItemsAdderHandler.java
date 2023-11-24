package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.List;

public enum ItemsAdderHandler implements Listener {

    INSTANCE;

    @EventHandler
    public void onItemsAdderLoaded(ItemsAdderLoadDataEvent event) {
        RecipeManager.reloadRecipeManager();
    }

}
