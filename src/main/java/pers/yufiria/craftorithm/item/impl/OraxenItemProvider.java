package pers.yufiria.craftorithm.item.impl;

import pers.yufiria.craftorithm.item.ItemProvider;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemUpdater;
import org.bukkit.OfflinePlayer;
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
        ItemStack built = OraxenItems.getItemById(itemName).build();
        return ItemUpdater.updateItem(built);
    }

    @Override
    public @Nullable ItemStack getItem(String itemName, OfflinePlayer player) {
        return getItem(itemName);
    }

}
