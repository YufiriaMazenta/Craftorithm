package pers.yufiria.craftorithm.ui;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

/**
 * 基础结果ID解析器
 */
public final class SimpleResultIdResolver implements ResultIdResolver {

    public static final SimpleResultIdResolver INSTANCE = new SimpleResultIdResolver();

    private SimpleResultIdResolver() {
    }

    @Override
    public @Nullable NamespacedItemIdStack resolve(@NotNull ItemStack item) {
        return ItemManager.INSTANCE.matchItemIdOrCreate(item, false);
    }

}
