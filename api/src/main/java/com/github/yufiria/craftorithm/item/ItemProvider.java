package com.github.yufiria.craftorithm.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemProvider {

    @NotNull
    String namespace();

    @Nullable
    String getItemName(ItemStack itemStack, boolean ignoreAmount);

    @Nullable
    ItemStack getItem(String itemName);

}
