package pers.yufiria.craftorithm.recipe.nms;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

/**
 * 单槽配方的通用实现：熔炉、烟熏、高炉、营火、切石
 * <p>
 * 各版本的配方类只需提供自己的材料，匹配逻辑由{@link NmsSingleItemRecipe#matches(ItemStack, World)}进行
 */
public interface NmsSingleItemRecipe extends NmsRecipe<ItemStack> {

    /**
     * 配方的输入材料，由版本侧的字段提供
     */
    RecipeChoice ingredientChoice();

    @Override
    default boolean matches(ItemStack input, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{input}, recipeKey())) {
            return false;
        }
        return ingredientChoice().test(input);
    }

}