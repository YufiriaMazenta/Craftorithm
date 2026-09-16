package pers.yufiria.craftorithm.hook.item;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public enum NexoItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public String namespace() {
        return "nexo";
    }

    @Override
    public NamespacedItemId matchItemId(ItemStack itemStack) {
        if (!NexoItems.exists(itemStack))
            return null;
        String itemId = NexoItems.idFromItem(itemStack);
        if (itemId == null) {
            return null;
        }
        return NamespacedItemId.of(
            namespace(),
            itemId
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