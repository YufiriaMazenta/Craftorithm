package pers.yufiria.craftorithm.item.impl;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum NexoItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public String namespace() {
        return "nexo";
    }

    @Override
    public NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        if (!NexoItems.exists(itemStack))
            return null;
        String itemId = NexoItems.idFromItem(itemStack);
        if (itemId == null) {
            return null;
        }
        if (ignoreAmount) {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemId
                )
            );
        } else {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemId
                ),
                itemStack.getAmount()
            );
        }
    }

    @Override
    public ItemStack matchItem(String itemName) {
        ItemBuilder itemBuilder = NexoItems.itemFromId(itemName);
        if (itemBuilder == null) {
            return null;
        }
        return itemBuilder.build();
    }

    @Override
    public ItemStack matchItem(String itemName, OfflinePlayer player) {
        return matchItem(itemName);
    }

}