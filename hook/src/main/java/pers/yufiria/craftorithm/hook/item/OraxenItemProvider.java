package pers.yufiria.craftorithm.hook.item;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemUpdater;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public enum OraxenItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "oraxen";
    }

    @Override
    public @Nullable NamespacedItemId matchItemId(ItemStack itemStack) {
        if (!OraxenItems.exists(itemStack))
            return null;
        String itemName = OraxenItems.getIdByItem(itemStack);
        return NamespacedItemId.of(
            namespace(),
            itemName
        );
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        if (!OraxenItems.exists(itemId)) {
            return null;
        }
        ItemStack built = OraxenItems.getItemById(itemId).build();
        return ItemUpdater.updateItem(built);
    }



}
