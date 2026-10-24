package pers.yufiria.craftorithm.ui;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 将配方材料物品解析为材料ID
 */
@FunctionalInterface
public interface IngredientIdResolver {

    /**
     * 解析材料物品的ID
     * @param item 材料物品
     * @return 材料ID, 无法解析时返回null
     */
    @Nullable
    String resolve(@Nullable ItemStack item);

}
