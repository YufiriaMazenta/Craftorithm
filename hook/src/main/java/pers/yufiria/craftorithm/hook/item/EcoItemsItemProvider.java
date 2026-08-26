package pers.yufiria.craftorithm.hook.item;

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import com.willfp.ecoitems.items.ItemUtilsKt;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum EcoItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "ecoitems";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        EcoItem ecoItem = ItemUtilsKt.getEcoItem(itemStack);
        if (ecoItem == null) {
            return null;
        }
        String id = ecoItem.getID();
        return new NamespacedItemIdStack(
            NamespacedItemId.of(
                namespace(),
                id
            ),
            ignoreAmount ? 1 : itemStack.getAmount()
        );
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        EcoItem ecoItem = EcoItems.INSTANCE.getByID(itemId);
        if (ecoItem == null) {
            return null;
        }
        return ecoItem.getItemStack();
    }


}
