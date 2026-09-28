package pers.yufiria.craftorithm.recipe.nms.common;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 工作台合成的输入内容
 */
public record BukkitCraftingInput(List<ItemStack> items, int width, int height, int ingredientCount) {

    /**
     * 供1.21之前的版本使用的构造函数, 构造时计算非air数量而非使用nms提供的数量
     */
    @Deprecated
    public BukkitCraftingInput(List<ItemStack> items, int width, int height) {
        this(items, width, height, (int) items.stream().filter(it -> !ItemHelper.isAir(it)).count());
    }
}