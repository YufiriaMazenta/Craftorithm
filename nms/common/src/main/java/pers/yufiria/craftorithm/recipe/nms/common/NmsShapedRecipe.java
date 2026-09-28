package pers.yufiria.craftorithm.recipe.nms.common;

import crypticlib.MinecraftVersion;
import org.bukkit.World;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.worldisolation.WorldIsolationDataHandler;

/**
 * 有序配方的通用实现
 * <p>
 * 各版本的配方类只需把NMS输入转换为 {@link BukkitCraftingInput}，匹配逻辑由 {@link CustomShapedRecipePattern} 提供
 */
public interface NmsShapedRecipe<Input> extends NmsCraftingRecipe<Input> {

    /**
     * 配方的形状与材料，由版本侧的字段提供
     */
    CustomShapedRecipePattern customPattern();

    @Override
    default boolean matches(Input input, World world) {
        if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
            return false;
        }
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21)) {
            if (!WorldIsolationDataHandler.INSTANCE.canRecipeUse(recipeKey(), world)) {
                return false;
            }
            BukkitCraftingInput craftingInput = toBukkitCraftingInput(input);
            return customPattern().matchesSince1_21(craftingInput.items(), craftingInput.width(), craftingInput.height(), craftingInput.ingredientCount());
        } else {
            BukkitCraftingInput craftingInput = toBukkitCraftingInput(input);
            if (!ItemManager.INSTANCE.canCraft(craftingInput.items(), recipeKey())) {
                return false;
            }
            return customPattern().matchesBefore1_21(craftingInput.items(), craftingInput.width(), craftingInput.height());
        }
    }

}
