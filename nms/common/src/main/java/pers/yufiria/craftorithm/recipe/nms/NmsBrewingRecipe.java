package pers.yufiria.craftorithm.recipe.nms;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitBrewingInput;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

/**
 * 酿造配方的通用实现：药水槽与材料槽都要匹配
 * <p>
 * 各版本的配方类只需提供自己的两个材料，匹配逻辑由{@link NmsBrewingRecipe#matches(BukkitBrewingInput, World)}进行
 */
public interface NmsBrewingRecipe extends NmsRecipe<BukkitBrewingInput> {

    /**
     * 药水槽的材料，由版本侧的字段提供
     */
    RecipeChoice inputChoice();

    /**
     * 材料槽的材料，由版本侧的字段提供
     */
    RecipeChoice reagentChoice();

    @Override
    default boolean matches(BukkitBrewingInput brewingInput, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{brewingInput.input(), brewingInput.reagent()}, recipeKey())) {
            return false;
        }
        return inputChoice().test(brewingInput.input())
            && reagentChoice().test(brewingInput.reagent());
    }

}