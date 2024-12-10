package pers.yufiria.craftorithm;

import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.CraftorithmItemProvider;
import org.bukkit.inventory.ItemStack;

public enum CraftorithmAPI {

    INSTANCE;

    public ItemStack getCraftorithmItem(String itemName) {
        return CraftorithmItemProvider.INSTANCE.matchItem(itemName);
    }

    public void regItemProvider(ItemProvider itemProvider) {
        ItemManager.INSTANCE.regItemProvider(itemProvider);
    }


}
