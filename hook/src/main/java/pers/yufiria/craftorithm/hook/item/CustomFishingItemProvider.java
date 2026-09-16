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

public enum CustomFishingItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "custom_fishing";
    }

    @Override
    public @Nullable NamespacedItemId matchItemId(ItemStack itemStack) {
        String itemId = BukkitCustomFishingPlugin.getInstance().getItemManager().getCustomFishingItemID(itemStack);
        if (itemId == null) {
            return null;
        }
        return NamespacedItemId.of(namespace(), itemId);
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
