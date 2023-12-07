package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public enum MythicMobsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "mythic_mobs";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        ItemExecutor itemExecutor = MythicBukkit.inst().getItemManager();
        if (!itemExecutor.isMythicItem(itemStack))
            return null;
        return itemExecutor.getMythicTypeFromItem(itemStack);
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        ItemExecutor executor = MythicBukkit.inst().getItemManager();
        Optional<MythicItem> itemOptional = executor.getItem(itemName);
        if (!itemOptional.isPresent()) {
            return null;
        }
        MythicItem mythicItem = itemOptional.get();
        int amount = mythicItem.getAmount();
        return BukkitAdapter.adapt(itemOptional.get().generateItemStack(amount));
    }
}
