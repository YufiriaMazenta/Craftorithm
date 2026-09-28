package pers.yufiria.craftorithm.recipe.nms;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitSmithingInput;
import pers.yufiria.craftorithm.util.IngredientUtils;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

import java.util.Optional;

/**
 * 锻造配方的通用实现：用 {@link RecipeChoice} 匹配锻造台的三个槽位
 * <p>
 * 各版本的配方类只需实现三个取值方法，匹配由{@link NmsSmithingRecipe#matches(BukkitSmithingInput, World)}进行
 */
public interface NmsSmithingRecipe extends NmsRecipe<BukkitSmithingInput> {

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

    default boolean matches(BukkitSmithingInput smithingInput, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        ItemStack template = smithingInput.template();
        ItemStack base = smithingInput.base();
        ItemStack addition = smithingInput.addition();
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{
            template, base, addition
        }, recipeKey())) {
            return false;
        }
        return IngredientUtils.testOptionalChoice(templateChoice(), template)
            && IngredientUtils.testOptionalChoice(baseChoice(), base)
            && IngredientUtils.testOptionalChoice(additionChoice(), addition);
    }

}