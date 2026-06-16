package pers.yufiria.craftorithm.hook.item;

import github.saukiya.sxitem.SXItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum SXItemItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "sx_items";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        String itemKey = SXItem.getItemManager().getItemKey(itemStack);
        if (itemKey == null) {
            return null;
        }
        if (ignoreAmount)
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemKey
                )
            );
        else {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemKey
                ),
                itemStack.getAmount()
            );
        }
    }

    @Override
    public @NotNull ItemStack matchItem(String itemId) {
        return SXItem.getItemManager().getItem(itemId);
    }

    @Override
    public @NotNull ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        return SXItem.getItemManager().getItem(itemId, player);
    }

}
