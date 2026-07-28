package pers.yufiria.craftorithm.hook.item;

import emaki.jiuwu.craft.item.api.EmakiItemApi;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum EmakiItemItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "emaikiitem";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        String identify = EmakiItemApi.identify(itemStack);
        if (identify == null){
            return null;
        }
        if (ignoreAmount) {
            return new NamespacedItemIdStack(new NamespacedItemId(namespace(), identify));
        } else {
            return new NamespacedItemIdStack(new NamespacedItemId(namespace(), identify), itemStack.getAmount());
        }
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        return EmakiItemApi.create(itemId, 1);
    }
}
