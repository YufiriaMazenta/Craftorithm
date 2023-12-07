package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MinecraftItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "minecraft";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        String itemName = itemStack.getType().name();
        if (itemStack.getAmount() > 1) {
            itemName += " " + itemStack.getAmount();
        }
        return itemName;
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        Material material = Material.matchMaterial(itemName);
        if (material == null)
            return null;
        return new ItemStack(material);
    }
}
