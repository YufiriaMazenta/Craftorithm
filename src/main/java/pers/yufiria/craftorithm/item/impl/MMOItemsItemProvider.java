package pers.yufiria.craftorithm.item.impl;

import pers.yufiria.craftorithm.item.ItemProvider;
import crypticlib.util.ItemHelper;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MMOItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "mmoitems";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        NBTItem nbtItem = NBTItem.get(itemStack);
        if (!nbtItem.hasType())
            return null;
        String type = nbtItem.getType();
        String id = nbtItem.getString("MMOITEMS_ITEM_ID");
        String itemKey = type + ":" + id;
        if (ignoreAmount) {
            return itemKey;
        } else {
            ItemStack miItem = getItem(itemKey);
            if (ItemHelper.isAir(miItem)) {
                return null;
            }
            return itemKey + " " + (itemStack.getAmount() / miItem.getAmount());
        }

    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        if (!itemName.contains(":"))
            return null;
        int index = itemName.indexOf(":");
        String type = itemName.substring(0, index);
        String id = itemName.substring(index + 1);
        return MMOItems.plugin.getItem(type, id);
    }

    @Override
    public @Nullable ItemStack getItem(String itemName, OfflinePlayer player) {
        return getItem(itemName);
    }

}
