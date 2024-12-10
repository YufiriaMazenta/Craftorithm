package pers.yufiria.craftorithm.item.impl;

import pers.yufiria.craftorithm.item.ItemProvider;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum MMOItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "mmoitems";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        NBTItem nbtItem = NBTItem.get(itemStack);
        if (!nbtItem.hasType())
            return null;
        String type = nbtItem.getType();
        String id = nbtItem.getString("MMOITEMS_ITEM_ID");
        String itemKey = type + ":" + id;
        if (ignoreAmount) {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemKey
                )
            );
        } else {
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
    public @Nullable ItemStack matchItem(String itemId) {
        if (!itemId.contains(":"))
            return null;
        int index = itemId.indexOf(":");
        String type = itemId.substring(0, index);
        String id = itemId.substring(index + 1);
        return MMOItems.plugin.getItem(type, id);
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, OfflinePlayer player) {
        return matchItem(itemId);
    }

}
