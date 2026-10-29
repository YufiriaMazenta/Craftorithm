package pers.yufiria.craftorithm.ui;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.CraftorithmItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

/**
 * 保存全部组件的结果ID解析器
 * <p>
 * 与 {@link SimpleResultIdResolver} 不同, 该解析器不会优先使用物品原本的物品源id,
 * 而是把结果物品的全部组件完整保存到内置物品源, 供「保存结果物品全部组件」开关使用。
 */
public enum SaveItemResultIdResolver implements ResultIdResolver {

    INSTANCE;

    @Override
    public @Nullable NamespacedItemIdStack resolveResultId(@NotNull ItemStack item) {
        return CraftorithmItemProvider.INSTANCE.saveItem(item, false);
    }

}
