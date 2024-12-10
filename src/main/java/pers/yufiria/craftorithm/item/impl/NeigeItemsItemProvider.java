package pers.yufiria.craftorithm.item.impl;

import pers.yufiria.craftorithm.item.ItemProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum NeigeItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "neige_items";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        ItemInfo niItemInfo = ItemManager.INSTANCE.isNiItem(itemStack);
        if (niItemInfo == null) {
            return null;
        }
        String id = niItemInfo.getId();
        if (ignoreAmount)
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    id
                )
            );
        else {
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
        if (!ItemManager.INSTANCE.hasItem(itemId))
            return null;
        return ItemManager.INSTANCE.getItemStack(itemId);
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, OfflinePlayer player) {
        if (!ItemManager.INSTANCE.hasItem(itemId))
            return null;
        return ItemManager.INSTANCE.getItemStack(itemId, player);
    }


}
