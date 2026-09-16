package pers.yufiria.craftorithm.hook.item;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public enum CraftEngineItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "craft_engine";
    }

    @Override
    public @Nullable NamespacedItemId matchItemId(ItemStack itemStack) {
        if (!CraftEngineItems.isCustomItem(itemStack)) {
            return null;
        }
        @Nullable Key craftEngineItemId = CraftEngineItems.getCustomItemId(itemStack);
        if (craftEngineItemId == null) {
            return null;
        }
        return NamespacedItemId.of(
            namespace(),
            craftEngineItemId.asString()
        );
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        BukkitItemDefinition craftEngineItem = CraftEngineItems.byId(itemId);
        if (craftEngineItem == null) {
            return null;
        }
        return craftEngineItem.buildBukkitItem();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        BukkitItemDefinition craftEngineItem = CraftEngineItems.byId(itemId);
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
