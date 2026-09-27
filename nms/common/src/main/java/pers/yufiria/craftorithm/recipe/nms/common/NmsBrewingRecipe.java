package pers.yufiria.craftorithm.recipe.nms.common;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

/**
 * 酿造配方的通用实现：药水槽与材料槽都要匹配
 * <p>
 * 各版本的配方类只需提供自己的两个材料，匹配逻辑由默认方法提供
 */
public interface NmsBrewingRecipe extends NmsRecipe {

    /**
     * 药水槽的材料，由版本侧的字段提供
     */
    RecipeChoice inputChoice();

    /**
     * 材料槽的材料，由版本侧的字段提供
     */
    RecipeChoice reagentChoice();

    /**
     * 药水槽与材料槽都必须匹配
     * <p>
     * {@code world} 是转换后的 Bukkit 世界，供将来按世界维度等条件做判定
     */
    default boolean matchesBrewing(ItemStack input, ItemStack reagent, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        if (!ItemManager.INSTANCE.canCraft(new ItemStack[]{input, reagent}, recipeKey())) {
            return false;
        }
        return inputChoice().test(input)
            && reagentChoice().test(reagent);
    }

}