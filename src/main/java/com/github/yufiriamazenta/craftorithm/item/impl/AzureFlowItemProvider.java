package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import io.rokuko.azureflow.api.AzureFlowAPI;
import io.rokuko.azureflow.features.item.AzureFlowItem;
import io.rokuko.azureflow.features.item.factory.AzureFlowItemFactory;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum AzureFlowItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "azureflow";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        AzureFlowItem azureFlowItem = AzureFlowAPI.toItem(itemStack);
        if (azureFlowItem == null) {
            return null;
        }
        String itemId = azureFlowItem.getUuid();
        if (ignoreAmount) {
            return itemId;
        } else {
            return itemId + itemStack.getAmount() / Objects.requireNonNull(getItem(itemId)).getAmount();
        }
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        AzureFlowItemFactory factory = AzureFlowAPI.getFactory(itemName);
        if (factory == null) {
            return null;
        }
        AzureFlowItem azureFlowItem = factory.build();
        return azureFlowItem.staticItemStack();
    }

    @Override
    public @Nullable ItemStack getItem(String itemName, @Nullable OfflinePlayer player) {
        AzureFlowItemFactory factory = AzureFlowAPI.getFactory(itemName);
        if (factory == null) {
            return null;
        }
        AzureFlowItem azureFlowItem = factory.build();
        if (player == null) {
            return azureFlowItem.staticItemStack();
        }
        return azureFlowItem.virtualItemStack(player.getPlayer());
    }
}
