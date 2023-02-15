package top.oasismc.oasisrecipe.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public class MenuHolder implements InventoryHolder {

    private Inventory inventory;
    private Map<Integer, MenuIcon> menuIconMap;

    public MenuHolder() {}

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
}
