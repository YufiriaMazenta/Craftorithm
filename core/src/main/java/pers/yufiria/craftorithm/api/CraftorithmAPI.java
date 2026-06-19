package pers.yufiria.craftorithm.api;

import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.CraftorithmItemProvider;

public enum CraftorithmAPI {

    INSTANCE;

    public ItemStack getCraftorithmItem(String itemName) {
        return CraftorithmItemProvider.INSTANCE.matchItem(itemName);
    }

}
