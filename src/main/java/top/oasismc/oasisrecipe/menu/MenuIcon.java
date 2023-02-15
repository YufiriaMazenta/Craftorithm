package top.oasismc.oasisrecipe.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class MenuIcon implements top.oasismc.oasisrecipe.api.menu.IMenuIcon {

    private ItemStack displayItem;
    private Function<InventoryClickEvent, Boolean> iconFunc;

    public MenuIcon(ItemStack displayItem, Function<InventoryClickEvent, Boolean> iconFunc) {
        this.displayItem = displayItem;
        this.iconFunc = iconFunc;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public Function<InventoryClickEvent, Boolean> getIconFunc() {
        return iconFunc;
    }

    public void setIconFunc(Function<InventoryClickEvent, Boolean> iconFunc) {
        this.iconFunc = iconFunc;
    }

    public boolean executeIconFunc(InventoryClickEvent event) {
        return iconFunc.apply(event);
    }

}
