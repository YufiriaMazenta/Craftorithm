package com.github.yufiriamazenta.craftorithm.item.impl;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;

public enum CraftEngineItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "craft_engine";
    }

    @Override
    public String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        if (!CraftEngineItems.isCustomItem(itemStack)) {
            return null;
        }
        @Nullable Key craftEngineItemId = CraftEngineItems.getCustomItemId(itemStack);
        if (craftEngineItemId == null) {
            return null;
        }
        String itemId = craftEngineItemId.asString();

        if (ignoreAmount) {
            return itemId;
        }
        return itemId + " " + itemStack.getAmount();
    }

    @Override
    public ItemStack getItem(String itemName) {
        BukkitItemDefinition craftEngineItem = CraftEngineItems.byId(itemName);
        if (craftEngineItem == null) {
            return null;
        }
        return craftEngineItem.buildBukkitItem();
    }

    @Override
    public ItemStack getItem(String itemName, OfflinePlayer player) {
        BukkitItemDefinition craftEngineItem = CraftEngineItems.byId(itemName);
        if (craftEngineItem == null) {
            return null;
        }
        if (player instanceof Player bukkitPlayer) {
            return craftEngineItem.buildBukkitItem(bukkitPlayer);
        } else {
            return craftEngineItem.buildBukkitItem();
        }
    }

}
