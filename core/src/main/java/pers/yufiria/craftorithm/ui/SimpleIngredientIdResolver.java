package pers.yufiria.craftorithm.ui;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemGroup;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Optional;

/**
 * 基础材料ID解析器, 不将物品数量计入材料ID
 */
public enum SimpleIngredientIdResolver implements IngredientIdResolver {

    INSTANCE;

    @Override
    public @Nullable String resolveIngredientId(@Nullable ItemStack item) {
        if (ItemHelper.isAir(item)) {
            return null;
        }
        Optional<ItemGroup> itemGroupOpt = ItemGroup.fromItemStack(item);
        if (itemGroupOpt.isPresent()) {
            return itemGroupOpt.get().toIngredientId();
        }
        NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemIdOrCreate(item, true);
        return itemId != null ? itemId.toString() : null;
    }

}
