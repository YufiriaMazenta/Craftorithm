package pers.yufiria.craftorithm.item.impl;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum ItemsAdderItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "items_adder";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null)
            return null;
        String id = customStack.getNamespacedID();
        if (ignoreAmount) {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    id
                )
            );
        } else {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    id
                ),
                itemStack.getAmount()
            );
        }
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        CustomStack customStack = CustomStack.getInstance(itemId);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, OfflinePlayer player) {
        return matchItem(itemId);
    }

}
