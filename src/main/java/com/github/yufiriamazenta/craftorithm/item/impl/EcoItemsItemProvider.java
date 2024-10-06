package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import com.willfp.ecoitems.items.ItemUtilsKt;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum EcoItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "ecoitems";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        EcoItem ecoItem = ItemUtilsKt.getEcoItem(itemStack);
        if (ecoItem == null) {
            return null;
        }
        String id = ecoItem.getID();
        if (ignoreAmount) {
            return id;
        } else {
            return id + " " + (itemStack.getAmount() / Objects.requireNonNull(getItem(id)).getAmount());
        }
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        EcoItem ecoItem = EcoItems.INSTANCE.getByID(itemName);
        if (ecoItem == null) {
            return null;
        }
        return ecoItem.getItemStack();
    }

    @Override
    public @Nullable ItemStack getItem(String itemName, @Nullable OfflinePlayer player) {
        return getItem(itemName);
    }
}
