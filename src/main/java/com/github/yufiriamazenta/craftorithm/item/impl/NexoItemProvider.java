package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public enum NexoItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public String namespace() {
        return "nexo";
    }

    @Override
    public String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        if (!NexoItems.exists(itemStack))
            return null;
        String itemId = NexoItems.idFromItem(itemStack);
        if (itemId == null) {
            return null;
        }
        if (ignoreAmount) {
            return itemId;
        } else {
            return itemId + " " + itemStack.getAmount() / Objects.requireNonNull(getItem(itemId)).getAmount();
        }
    }

    @Override
    public ItemStack getItem(String itemName) {
        ItemBuilder itemBuilder = NexoItems.itemFromId(itemName);
        if (itemBuilder == null) {
            return null;
        }
        return itemBuilder.build();
    }

    @Override
    public ItemStack getItem(String itemName, OfflinePlayer player) {
        return getItem(itemName);
    }

}
