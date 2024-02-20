package com.github.yufiria.craftorithm.recipe.handler;

import crypticlib.listener.BukkitListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

@BukkitListener
public class ShapedRecipeHandler implements Listener {

    @EventHandler
    public void onPrepareCrafting(PrepareItemCraftEvent event) {
        //TODO 识别
    }

}
