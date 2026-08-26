package pers.yufiria.craftorithm.hook.item;

import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.context.Context;
import net.momirealms.customfishing.api.mechanic.context.ContextKeys;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum CustomFishingItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "custom_fishing";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        String itemId = BukkitCustomFishingPlugin.getInstance().getItemManager().getCustomFishingItemID(itemStack);
        if (itemId == null) {
            return null;
        }
        return new NamespacedItemIdStack(
            NamespacedItemId.of(namespace(), itemId),
            ignoreAmount ? 1 : itemStack.getAmount()
        );
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        return matchItem(itemId, null);
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, @Nullable OfflinePlayer player) {
        Player bukkitPlayer = player instanceof Player onlinePlayer ? onlinePlayer : null;
        Context<Player> context = Context.player(bukkitPlayer).arg(ContextKeys.ID, itemId);
        return BukkitCustomFishingPlugin.getInstance().getItemManager().buildAny(context, "CustomFishing:" + itemId);
    }

}
