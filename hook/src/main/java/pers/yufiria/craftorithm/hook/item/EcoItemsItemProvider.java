package pers.yufiria.craftorithm.hook.item;

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import com.willfp.ecoitems.items.ItemUtilsKt;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public enum EcoItemsItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "ecoitems";
    }

    @Override
    public @Nullable NamespacedItemId matchItemId(ItemStack itemStack) {
        EcoItem ecoItem = ItemUtilsKt.getEcoItem(itemStack);
        if (ecoItem == null) {
            return null;
        }
        String id = ecoItem.getID();
        return NamespacedItemId.of(
            namespace(),
            id
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
