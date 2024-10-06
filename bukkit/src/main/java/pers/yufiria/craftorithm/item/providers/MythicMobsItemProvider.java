package pers.yufiria.craftorithm.item.providers;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;

import java.util.Optional;

public enum MythicMobsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "mythic_mobs";
    }

    @Override
    public String matchItemId(ItemStack itemStack) {
        ItemExecutor itemExecutor = MythicBukkit.inst().getItemManager();
        if (!itemExecutor.isMythicItem(itemStack))
            return null;
        return itemExecutor.getMythicTypeFromItem(itemStack);
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        ItemExecutor executor = MythicBukkit.inst().getItemManager();
        Optional<MythicItem> itemOptional = executor.getItem(itemId);
        if (itemOptional.isEmpty()) {
            return null;
        }
        MythicItem mythicItem = itemOptional.get();
        int amount = mythicItem.getAmount();
        return BukkitAdapter.adapt(itemOptional.get().generateItemStack(amount));
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, OfflinePlayer player) {
        return matchItem(itemId);
    }

}
