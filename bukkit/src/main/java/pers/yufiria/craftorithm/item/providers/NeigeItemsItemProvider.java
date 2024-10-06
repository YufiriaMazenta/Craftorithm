package pers.yufiria.craftorithm.item.providers;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;

public enum NeigeItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "neige_items";
    }

    @Override
    public String matchItemId(ItemStack itemStack) {
        ItemInfo niItemInfo = ItemManager.INSTANCE.isNiItem(itemStack);
        if (niItemInfo == null) {
            return null;
        }
        return niItemInfo.getId();
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
