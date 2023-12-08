package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ItemsAdderItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "items_adder";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null)
            return null;
        if (ignoreAmount) {
            return customStack.getId();
        } else {
            return customStack.getId() + " " + (itemStack.getAmount() / customStack.getItemStack().getAmount());
        }
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        CustomStack customStack = CustomStack.getInstance(itemName);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }
}
