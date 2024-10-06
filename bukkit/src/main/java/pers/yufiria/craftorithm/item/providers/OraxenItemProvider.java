package pers.yufiria.craftorithm.item.providers;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemUpdater;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;

public enum OraxenItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "oraxen";
    }

    @Override
    public String matchItemId(ItemStack itemStack) {
        if (!OraxenItems.exists(itemStack))
            return null;
        return OraxenItems.getIdByItem(itemStack);
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
