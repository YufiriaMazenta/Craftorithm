package pers.yufiria.craftorithm.ui;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

/**
 * 将配方结果物品解析为结果ID
 */
@FunctionalInterface
public interface ResultIdResolver {

    /**
     * 解析结果物品的ID
     *
     * @param item 结果物品
     * @return 结果ID, 无法解析时返回null
     */
    @Nullable
    NamespacedItemIdStack resolve(@NotNull ItemStack item);

}
