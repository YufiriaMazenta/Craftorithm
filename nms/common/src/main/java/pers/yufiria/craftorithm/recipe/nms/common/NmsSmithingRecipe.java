package pers.yufiria.craftorithm.recipe.nms.common;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.util.IngredientUtils;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

import java.util.Optional;

/**
 * 锻造配方的通用实现：用 {@link RecipeChoice} 匹配锻造台的三个槽位
 * <p>
 * 各版本的配方类只需实现三个取值方法，匹配算法由默认方法提供
 */
public interface NmsSmithingRecipe extends NmsRecipe {

    /**
     * 模板槽位的材料，可以为空
     */
    Optional<RecipeChoice> templateChoice();

    /**
     * 基础槽位的材料，可以为空
     */
    Optional<RecipeChoice> baseChoice();

    /**
     * 追加槽位的材料，可以为空
     */
    Optional<RecipeChoice> additionChoice();

    /**
     * 三个槽位的材料必须全部匹配
     * <p>
     * {@code world} 是转换后的 Bukkit 世界，供将来按世界维度等条件做判定
     */
    default boolean matchesSmithing(ItemStack templateInput, ItemStack baseInput, ItemStack additionInput, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{
            templateInput, baseInput, additionInput
        }, recipeKey())) {
            return false;
        }
        return IngredientUtils.testOptionalChoice(templateChoice(), templateInput)
            && IngredientUtils.testOptionalChoice(baseChoice(), baseInput)
            && IngredientUtils.testOptionalChoice(additionChoice(), additionInput);
    }

}