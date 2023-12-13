package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import crypticlib.util.ItemUtil;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
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
            ItemStack item = getItem(type + ":" + id);
            if (ItemUtil.isAir(item)) {
                return null;
            }
            return type + id + " " + (itemStack.getAmount() / item.getAmount());
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
}
