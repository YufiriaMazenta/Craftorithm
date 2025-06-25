package pers.yufiria.craftorithm.item.impl;

import net.momirealms.craftengine.bukkit.api.BukkitAdaptors;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum CraftEngineItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "craft_engine";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        if (!CraftEngineItems.isCustomItem(itemStack)) {
            return null;
        }
        @Nullable Key craftEngineItemId = CraftEngineItems.getCustomItemId(itemStack);
        if (craftEngineItemId == null) {
            return null;
        }
        NamespacedItemIdStack stack = new NamespacedItemIdStack(
            new NamespacedItemId(
                namespace(),
                craftEngineItemId.asString()
            )
        );
        if (ignoreAmount) {
            return stack;
        }
        stack.setAmount(itemStack.getAmount());
        return stack;
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        Key craftEngineItemId = Key.from(itemId);
        CustomItem<ItemStack> craftEngineItem = CraftEngineItems.byId(craftEngineItemId);
        if (craftEngineItem == null) {
            return null;
        }
        return craftEngineItem.buildItemStack();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        Key craftEngineItemId = Key.from(itemId);
        CustomItem<ItemStack> craftEngineItem = CraftEngineItems.byId(craftEngineItemId);
        if (craftEngineItem == null) {
            return null;
        }
        if (player == null) {
            return craftEngineItem.buildItemStack();
        }

        Player bukkitPlayer = player.getPlayer();
        BukkitServerPlayer craftEnginePlayer = BukkitAdaptors.adapt(bukkitPlayer);
        return craftEngineItem.buildItemStack(craftEnginePlayer);
    }

}
