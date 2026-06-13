package pers.yufiria.craftorithm.item;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemProvider {

    @NotNull
    String namespace();

    @Nullable
    NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount);

    @Nullable
    ItemStack matchItem(String itemId);

    @Nullable
    ItemStack matchItem(String itemId, @Nullable OfflinePlayer player);

}
