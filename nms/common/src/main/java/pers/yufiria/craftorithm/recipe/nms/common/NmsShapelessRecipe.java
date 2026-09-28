package pers.yufiria.craftorithm.recipe.nms.common;

import crypticlib.util.ItemHelper;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.util.IngredientUtils;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 无序配方的通用接口
 * <p>
 * 各版本的配方类只需实现 {@link NmsCraftingRecipe#toBukkitCraftingInput}，匹配由{@link NmsShapelessRecipe#matches(Object, World)}进行
 */
public interface NmsShapelessRecipe<Input> extends NmsCraftingRecipe<Input> {

    /**
     * 配方材料，由版本侧的字段提供
     */
    List<RecipeChoice> customIngredients();

    /**
     * 过滤空气后比较材料数量，再进行回溯匹配
     * <p>
     * {@code world} 是转换后的 Bukkit 世界，供将来按世界维度等条件做判定
     */
    @Override
    default boolean matches(Input input, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        List<ItemStack> bukkitInputItems = toBukkitCraftingInput(input).items();
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
