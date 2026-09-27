package pers.yufiria.craftorithm.recipe.nms.common;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * NMS 合成格的 Bukkit 视图：行优先展开的物品与宽高
 */
public record BukkitCraftingInput(List<ItemStack> items, int width, int height) {
}
