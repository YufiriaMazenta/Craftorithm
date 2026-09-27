package pers.yufiria.craftorithm.recipe.nms.common;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.worldisolation.WorldIsolationDataHandler;

/**
 * 有序配方的通用实现
 * <p>
 * 各版本的配方类只需把 NMS 合成格转换为 {@link BukkitCraftingInput}，匹配逻辑由 {@link CustomShapedRecipePattern} 提供
 */
public interface NmsShapedRecipe<Input> extends NmsCraftingRecipe<Input> {

    /**
     * 配方的形状与材料，由版本侧的字段提供
     */
    CustomShapedRecipePattern customPattern();

    /**
     * 1.20 ~ 1.20.5 的匹配方式：在合成格内滑动寻找匹配位置
     * <p>
     * {@code world} 是转换后的 Bukkit 世界，供将来按世界维度等条件做判定
     */
    default boolean matchesShapedBefore1_21(Input input, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        BukkitCraftingInput craftingInput = toBukkitCraftingInput(input);
        if (!ItemManager.INSTANCE.canCraft(craftingInput.items(), recipeKey())) {
            return false;
        }
        return customPattern().matchesBefore1_21(craftingInput.items(), craftingInput.width(), craftingInput.height());
    }

    /**
     * 1.21 起的匹配方式，因为在NMS的CraftingInput已经处理了边缘切割，无需进行额外偏移
     *
     * @param inputIngredientCount 合成格中的材料数，由版本侧从 NMS 取值传入
     * @param world               转换后的 Bukkit 世界，供将来按世界维度等条件做判定
     */
    default boolean matchesShapedSince1_21(Input input, int inputIngredientCount, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        BukkitCraftingInput craftingInput = toBukkitCraftingInput(input);
        return customPattern().matchesSince1_21(craftingInput.items(), craftingInput.width(), craftingInput.height(), inputIngredientCount);
    }

}
