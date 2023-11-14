package com.github.yufiriamazenta.craftorithm.menu.bukkit;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.menu.impl.recipe.RecipeDisplayMenuHolder;
import crypticlib.CrypticLib;
import crypticlib.annotations.BukkitListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;

@BukkitListener
public enum BukkitMenuDispatcher implements Listener {

    INSTANCE;

    private final List<InventoryAction> allowedPlayerInvActions;

    BukkitMenuDispatcher() {
        allowedPlayerInvActions = new ArrayList<>();
        allowedPlayerInvActions.add(InventoryAction.DROP_ALL_CURSOR);
        allowedPlayerInvActions.add(InventoryAction.DROP_ALL_SLOT);
        allowedPlayerInvActions.add(InventoryAction.DROP_ONE_CURSOR);
        allowedPlayerInvActions.add(InventoryAction.DROP_ONE_SLOT);
        allowedPlayerInvActions.add(InventoryAction.NOTHING);
        allowedPlayerInvActions.add(InventoryAction.PICKUP_ONE);
        allowedPlayerInvActions.add(InventoryAction.PICKUP_HALF);
        allowedPlayerInvActions.add(InventoryAction.PICKUP_ALL);
        allowedPlayerInvActions.add(InventoryAction.PICKUP_SOME);
        allowedPlayerInvActions.add(InventoryAction.PLACE_ONE);
        allowedPlayerInvActions.add(InventoryAction.PLACE_ALL);
        allowedPlayerInvActions.add(InventoryAction.PLACE_SOME);
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        InventoryView view = event.getView();
        BukkitMenuHandler handler;
        if (!(view.getTopInventory().getHolder() instanceof BukkitMenuHandler))
            return;
        handler = (BukkitMenuHandler) view.getTopInventory().getHolder();
        if (view.getBottomInventory().equals(event.getClickedInventory())) {
            InventoryAction action = event.getAction();
            if (!allowedPlayerInvActions.contains(action))
                event.setCancelled(true);
            return;
        }
        handler.click(event.getSlot(), event);
    }

    @EventHandler
    public void onCloseRecipeShowMenu(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof RecipeDisplayMenuHolder) {
            RecipeDisplayMenuHolder holder = (RecipeDisplayMenuHolder) event.getInventory().getHolder();
            if (holder.getParentMenu() != null) {
                CrypticLib.platform().scheduler().runTask(Craftorithm.getInstance(), () -> event.getPlayer().openInventory(holder.getParentMenu().getInventory()));
            }
        }
    }

}
