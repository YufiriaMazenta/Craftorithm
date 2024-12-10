package pers.yufiria.craftorithm.item.impl;

import pers.yufiria.craftorithm.item.ItemProvider;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.OfflinePlayer;
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
        String mmItemName = itemExecutor.getMythicTypeFromItem(itemStack);
        if (ignoreAmount)
            return mmItemName;
        else {
            ItemStack mmItem = itemExecutor.getItemStack(mmItemName);
            return mmItemName + " " + (itemStack.getAmount() / mmItem.getAmount());
        }
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

    @Override
    public @Nullable ItemStack getItem(String itemName, OfflinePlayer player) {
        return getItem(itemName);
    }

}
