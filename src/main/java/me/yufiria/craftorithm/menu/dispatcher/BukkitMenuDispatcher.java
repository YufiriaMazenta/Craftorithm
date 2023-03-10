package me.yufiria.craftorithm.menu.dispatcher;

import me.yufiria.craftorithm.Craftorithm;
import me.yufiria.craftorithm.menu.bukkit.BukkitMenuHandler;
import me.yufiria.craftorithm.menu.recipeshow.RecipeListMenuHolder;
import me.yufiria.craftorithm.menu.recipeshow.RecipeShowMenuHolder;
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
            event.setCancelled(true);
            return;
        }
        BukkitMenuHandler handler = (BukkitMenuHandler) view.getTopInventory().getHolder();
        handler.click(event.getSlot(), event);
    }

    @EventHandler
    public void onCloseRecipeShowMenu(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof RecipeShowMenuHolder) {
            RecipeShowMenuHolder holder = (RecipeShowMenuHolder) event.getInventory().getHolder();
            if (holder.getParentMenu() != null) {
                Bukkit.getScheduler().runTask(Craftorithm.getInstance(), () -> {
                    event.getPlayer().openInventory(holder.getParentMenu().getInventory());
                });
            }
        }
    }

}
