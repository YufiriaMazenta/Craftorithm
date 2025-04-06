package pers.yufiria.craftorithm;

import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.CraftorithmItemProvider;

public enum CraftorithmAPI {

    INSTANCE;

    public ItemStack getCraftorithmItem(String itemName) {
        return CraftorithmItemProvider.INSTANCE.matchItem(itemName);
    }

    public void regItemProvider(ItemProvider itemProvider) {
        ItemManager.INSTANCE.regItemProvider(itemProvider);
    }


}
