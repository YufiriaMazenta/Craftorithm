package pers.yufiria.craftorithm.item.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.InventoryViewHelper;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.fuel.CustomFuelManager;

@EventListener
public enum CustomFuelHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void putFuelOnClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        Inventory topInventory = InventoryViewHelper.getTopInventory(event);
        if (!(topInventory instanceof FurnaceInventory furnaceInventory))
            return;
        Inventory clickInv = event.getClickedInventory();
        ItemStack current = event.getCurrentItem();
        int slot = event.getSlot();
        switch (event.getClick()) {
            case LEFT:
            case RIGHT:
                if (!furnaceInventory.equals(clickInv)) {
                    return;
                }
                if (slot != 1)
                    return;
                ItemStack cursor = event.getCursor();
                if (ItemHelper.isAir(cursor))
                    return;
                if (cursor.getType().isFuel())
                    return;
                if (!CustomFuelManager.INSTANCE.isCustomFuel(cursor))
                    return;
                event.setCancelled(true);
                if (cursor.isSimilar(current)) {
                    int canPlaceAmount = current.getMaxStackSize() - current.getAmount();
                    //先算出实际转移量, 加减都用它, 否则光标数量会减到负数
                    int moved = Math.min(cursor.getAmount(), canPlaceAmount);
                    if (moved <= 0) {
                        return;
                    }
                    current.setAmount(current.getAmount() + moved);
                    cursor.setAmount(cursor.getAmount() - moved);
                    event.setCurrentItem(current);
                    event.setCursor(cursor.getAmount() > 0 ? cursor : null);
                } else {
                    event.setCursor(current);
                    event.setCurrentItem(cursor);
                }
                ((Player) event.getWhoClicked()).updateInventory();
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                if (furnaceInventory.equals(clickInv))
                    return;
                if (ItemHelper.isAir(current))
                    return;
                if (!CustomFuelManager.INSTANCE.isCustomFuel(current))
                    return;
                if (!ItemHelper.isAir(furnaceInventory.getFuel())) {
                    ItemStack fuel = furnaceInventory.getFuel();
                    if (!current.isSimilar(fuel)) return;
                    int canPlaceAmount = fuel.getMaxStackSize() - fuel.getAmount();
                    //同上: 用实际转移量做扣减, 避免把背包格减成负数
                    int moved = Math.min(current.getAmount(), canPlaceAmount);
                    if (moved <= 0) {
                        return;
                    }
                    fuel.setAmount(fuel.getAmount() + moved);
                    furnaceInventory.setFuel(fuel);
                    current.setAmount(current.getAmount() - moved);
                    event.setCurrentItem(current.getAmount() > 0 ? current : null);
                    return;
                } else {
                    event.setCancelled(true);
                    event.setCurrentItem(null);
                    furnaceInventory.setFuel(current);
                }
                ((Player) event.getWhoClicked()).updateInventory();
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void setFuelBurnTime(FurnaceBurnEvent event) {
        if (event.isCancelled())
            return;
        ItemStack fuel = event.getFuel();
        Integer time = CustomFuelManager.INSTANCE.matchBurnTime(fuel);
        if (time == null || time <= 0)
            return;
        event.setBurnTime(time);
    }

}
