package pers.yufiriamazenta.craftorithm;

import pers.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import pers.yufiriamazenta.craftorithm.item.ItemManager;
import pers.yufiriamazenta.craftorithm.item.ItemProvider;
import pers.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
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
