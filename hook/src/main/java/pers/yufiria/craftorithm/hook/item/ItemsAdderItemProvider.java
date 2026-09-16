package pers.yufiria.craftorithm.hook.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public enum ItemsAdderItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "items_adder";
    }

    @Override
    public @Nullable NamespacedItemId matchItemId(ItemStack itemStack) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null)
            return null;
        String id = customStack.getNamespacedID();
        return NamespacedItemId.of(
            namespace(),
            id
        );
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        CustomStack customStack = CustomStack.getInstance(itemId);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }

}
