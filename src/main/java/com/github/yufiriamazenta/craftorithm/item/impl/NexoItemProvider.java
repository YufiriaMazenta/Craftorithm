package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public enum NexoItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public String namespace() {
        return "nexo";
    }

    @Override
    public String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        return "";
    }

    @Override
    public ItemStack getItem(String itemName) {
        return null;
    }

    @Override
    public ItemStack getItem(String itemName, OfflinePlayer player) {
        return null;
    }

}
