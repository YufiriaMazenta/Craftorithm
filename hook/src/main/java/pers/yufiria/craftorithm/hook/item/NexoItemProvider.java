package pers.yufiria.craftorithm.hook.item;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
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
        return new NamespacedItemIdStack(
            NamespacedItemId.of(
                namespace(),
                itemId
            ),
            ignoreAmount ? 1 : itemStack.getAmount()
        );
    }

    @Override
    public ItemStack matchItem(String itemName) {
        ItemBuilder itemBuilder = NexoItems.itemFromId(itemName);
        if (itemBuilder == null) {
            return null;
        }
        return itemBuilder.build();
    }



}