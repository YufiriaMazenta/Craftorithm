package pers.yufiria.craftorithm.item.impl;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemUpdater;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum OraxenItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "oraxen";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        if (!OraxenItems.exists(itemStack))
            return null;
        String itemName = OraxenItems.getIdByItem(itemStack);
        if (ignoreAmount) {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemName
                )
            );
        } else {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemName
                ),
                itemStack.getAmount()
            );
        }
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        if (!OraxenItems.exists(itemId)) {
            return null;
        }
        ItemStack built = OraxenItems.getItemById(itemId).build();
        return ItemUpdater.updateItem(built);
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, OfflinePlayer player) {
        return matchItem(itemId);
    }

}
