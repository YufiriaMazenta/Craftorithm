package pers.yufiria.craftorithm.ui;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemGroup;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Optional;

/**
 * 堆叠材料ID解析器, 将物品数量计入材料ID
 */
public enum StackedIngredientIdResolver implements IngredientIdResolver {

    INSTANCE;

    @Override
    public @Nullable String resolveIngredientId(@Nullable ItemStack item) {
        if (ItemHelper.isAir(item)) {
            return null;
        }
        Optional<ItemGroup> itemGroupOpt = ItemGroup.fromItemStack(item);
        if (itemGroupOpt.isPresent()) {
            String ingredientId = itemGroupOpt.get().toIngredientId();
            return item.getAmount() <= 1 ? ingredientId : ingredientId + " " + item.getAmount();
        }
        NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemIdOrCreate(item, false);
        return itemId != null ? itemId.toString() : null;
    }

}
