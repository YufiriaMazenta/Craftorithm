package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
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
