package pers.yufiria.craftorithm;

import pers.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.CraftorithmItemProvider;
import org.bukkit.inventory.ItemStack;

public enum CraftorithmAPI {

    INSTANCE;

    public ItemStack getCraftorithmItem(String itemName) {
        return CraftorithmItemProvider.INSTANCE.getItem(itemName);
    }

    public void regItemProvider(ItemProvider itemProvider) {
        ItemManager.INSTANCE.regItemProvider(itemProvider);
    }

    public ArcencielDispatcher arcencielDispatcher() {
        return ArcencielDispatcher.INSTANCE;
    }

}
