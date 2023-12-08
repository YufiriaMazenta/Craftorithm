package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

public enum NeigeItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "neige_items";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        ItemInfo niItemInfo = ItemManager.INSTANCE.isNiItem(itemStack);
        if (niItemInfo == null) {
            return null;
        }
        if (ignoreAmount)
            return niItemInfo.getId();
        else {
            ItemStack niItem = ItemManager.INSTANCE.getItemStack(niItemInfo.getId());
            return niItemInfo.getId() + " " + (itemStack.getAmount() / niItem.getAmount());
        }
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        if (!ItemManager.INSTANCE.hasItem(itemName))
            return null;
        return ItemManager.INSTANCE.getItemStack(itemName);
    }
}
