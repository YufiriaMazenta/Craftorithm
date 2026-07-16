package pers.yufiria.craftorithm.recipe.nms;

import com.google.common.collect.Maps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftShapedRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import pers.yufiria.craftorithm.recipe.choice.CustomRecipeChoice;

import java.util.Map;
import java.util.Objects;

public final class ShapedRecipe12107 extends ShapedRecipes {

    private final ShapedRecipePattern12107 customPattern;

    ShapedRecipe12107(
        String group,
        CraftingBookCategory category,
        ShapedRecipePattern pattern,
        ItemStack result,
        ShapedRecipePattern12107 customPattern
    ) {
        super(group, category, pattern, result);
        this.customPattern = customPattern;
    }

    /**
     * 对应match方法
     */
    @Override
    public boolean a(CraftingInput input, World world) {
        return customPattern.matches(input);
    }

    public static RecipeHolder<ShapedRecipes> fromBukkit(NamespacedKey recipeKey, ShapedRecipe shapedRecipe) {
        CraftShapedRecipe craftRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        Map<Character, RecipeChoice> bukkitIngredients = craftRecipe.getChoiceMap();
        String[] shape = replaceUndefinedIngredientsWithEmpty(craftRecipe.getShape(), bukkitIngredients);
        bukkitIngredients.values().removeIf(Objects::isNull);
        Map<Character, RecipeItemStack> nmsIngredients = Maps.transformValues(bukkitIngredients, (recipeChoice) -> {
            if (recipeChoice instanceof CustomRecipeChoice customRecipeChoice) {
                return craftRecipe.toNMS(customRecipeChoice.bukkitChoice(), false);
            } else {
                return craftRecipe.toNMS(recipeChoice, false);
            }
        });
        net.minecraft.world.item.crafting.ShapedRecipePattern pattern = net.minecraft.world.item.crafting.ShapedRecipePattern.a(nmsIngredients, shape);
        ShapedRecipePattern12107 customPattern = ShapedRecipePattern12107.fromBukkitRecipe(shapedRecipe);

        ShapedRecipes nmsShapedRecipe = new ShapedRecipe12107(
            craftRecipe.getGroup(),
            CraftRecipe.getCategory(craftRecipe.getCategory()),
            pattern,
            CraftItemStack.asNMSCopy(shapedRecipe.getResult()),
            customPattern
        );
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), nmsShapedRecipe);
    }

    private static String[] replaceUndefinedIngredientsWithEmpty(String[] shape, Map<Character, RecipeChoice> ingredients) {
        for(int i = 0; i < shape.length; ++i) {
            String row = shape[i];
            StringBuilder filteredRow = new StringBuilder(row.length());

            for(char character : row.toCharArray()) {
                filteredRow.append(ingredients.get(character) == null ? ' ' : character);
            }

            shape[i] = filteredRow.toString();
        }

        return shape;
    }


}
