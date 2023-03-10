package me.yufiria.craftorithm.menu.dispatcher;

import me.yufiria.craftorithm.menu.bukkit.BukkitMenuHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

}
