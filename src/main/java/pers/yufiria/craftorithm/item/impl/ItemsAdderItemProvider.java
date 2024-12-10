package pers.yufiria.craftorithm.item.impl;

import pers.yufiria.craftorithm.item.ItemProvider;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.OfflinePlayer;
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
        String id = customStack.getNamespacedID();
        if (ignoreAmount) {
            return id;
        } else {
            return id + " " + (itemStack.getAmount() / getItem(id).getAmount());
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

    @Override
    public @Nullable ItemStack getItem(String itemName, OfflinePlayer player) {
        return getItem(itemName);
    }

}
