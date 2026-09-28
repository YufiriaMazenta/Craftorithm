package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.util.ItemHelper;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitCraftingInput;
import pers.yufiria.craftorithm.util.IngredientUtils;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 无序配方的通用接口
 * <p>
 * 各版本的配方类只需实现 {@link NmsCraftingRecipe#toBukkitCraftingInput}
 */
public interface NmsShapelessRecipe<NmsInput> extends NmsCraftingRecipe<NmsInput> {

    /**
     * 配方材料，由版本侧的字段提供
     */
    List<RecipeChoice> customIngredients();

    @Override
    default boolean matches(BukkitCraftingInput craftingInput, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        List<ItemStack> bukkitInputItems = craftingInput.items();
        List<RecipeChoice> ingredients = customIngredients();
        List<ItemStack> inputItems = new ArrayList<>(bukkitInputItems.size());
        for (ItemStack inputItem : bukkitInputItems) {
            if (!ItemHelper.isAir(inputItem)) {
                inputItems.add(inputItem);
            }
        }
        if (!ItemManager.INSTANCE.canCraft(inputItems, recipeKey())) {
            return false;
        }
        if (inputItems.size() != ingredients.size()) {
            return false;
        }
        if (inputItems.size() == 1) {
            return ingredients.getFirst().test(inputItems.getFirst());
        }
        return IngredientUtils.matchItemsToChoices(inputItems, ingredients);
    }

}
