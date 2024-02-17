package com.github.yufiria.craftorithm.item.impl;

import com.github.yufiria.craftorithm.item.ItemProvider;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum OraxenItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "oraxen";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        if (!OraxenItems.exists(itemStack))
            return null;
        String itemName = OraxenItems.getIdByItem(itemStack);
        if (ignoreAmount) {
            return itemName;
        } else {
            ItemStack oraxenItem = OraxenItems.getItemById(itemName).build();
            return itemName + " " + (itemStack.getAmount() / oraxenItem.getAmount());
        }
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        if (!OraxenItems.exists(itemName)) {
            return null;
        }
        return OraxenItems.getItemById(itemName).build();
    }
}
