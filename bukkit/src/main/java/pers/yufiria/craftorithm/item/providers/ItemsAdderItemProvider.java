package pers.yufiria.craftorithm.item.providers;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;

public enum ItemsAdderItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "items_adder";
    }

    @Override
    public String matchItemId(ItemStack itemStack) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null)
            return null;
        return customStack.getNamespacedID();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        CustomStack customStack = CustomStack.getInstance(itemId);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId, OfflinePlayer player) {
        return matchItem(itemId);
    }

}
