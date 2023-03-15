package com.github.yufiriamazenta.craftorithm.menu.bukkit;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.menu.impl.recipe.RecipeDisplayMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

public enum BukkitMenuDispatcher implements Listener {

    INSTANCE;

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        InventoryView view = event.getView();
        if (!(view.getTopInventory().getHolder() instanceof BukkitMenuHandler))
            return;
        if (view.getBottomInventory().equals(event.getClickedInventory())) {
            return;
        }
        BukkitMenuHandler handler = (BukkitMenuHandler) view.getTopInventory().getHolder();
        handler.click(event.getSlot(), event);
    }

    @EventHandler
    public void onCloseRecipeShowMenu(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof RecipeDisplayMenuHolder) {
            RecipeDisplayMenuHolder holder = (RecipeDisplayMenuHolder) event.getInventory().getHolder();
            if (holder.getParentMenu() != null) {
                Bukkit.getScheduler().runTask(Craftorithm.getInstance(), () -> {
                    event.getPlayer().openInventory(holder.getParentMenu().getInventory());
                });
            }
        }
    }

}
