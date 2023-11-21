package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import org.bukkit.inventory.ItemStack;

public enum CraftorithmAPI {

    INSTANCE;

    public ItemStack getCraftorithmItem(String itemName) {
        return ItemManager.getCraftorithmItem(itemName);
    }

    public ArcencielDispatcher arcencielDispatcher() {
        return ArcencielDispatcher.INSTANCE;
    }

}
