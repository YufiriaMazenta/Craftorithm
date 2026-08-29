package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import pers.yufiria.craftorithm.recipe.nms.CustomShapedRecipePattern;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ShapedRecipePattern260100 extends CustomShapedRecipePattern<CraftingInput> {

    public ShapedRecipePattern260100(int width, int height, List<Optional<RecipeChoice>> ingredients) {
        super(width, height, ingredients);
    }

    public static ShapedRecipePattern260100 fromBukkitRecipe(ShapedRecipe shapedRecipe) {
        String[] shape = shapedRecipe.getShape();
        Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
        int height = shape.length;
        int width = 0;
        for (String line : shape) {
            width = Math.max(line.length(), width);
        }
        List<Optional<RecipeChoice>> ingredients = new ArrayList<>(width * height);
        shrink(shape, width);
        for (String line : shape) {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == ' ') {
                    ingredients.add(Optional.empty());
                    continue;
                }
                RecipeChoice recipeChoice = choiceMap.get(c);
                ingredients.add(Optional.ofNullable(recipeChoice));
            }
        }
        return new ShapedRecipePattern260100(width, height, ingredients);
    }

    public boolean matches(CraftingInput craftingInput) {
        if (craftingInput.ingredientCount() != this.ingredientCount) return false;
        if (craftingInput.width() != width) return false;
        if (craftingInput.height() != height) return false;
        if (!symmetrical && this.matches(craftingInput, true)) return true;
        return this.matches(craftingInput, false);
    }

    public boolean matches(CraftingInput craftingInput, boolean symmetrical) {
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                Optional<RecipeChoice> ingredient;
                if (symmetrical) {
                    ingredient = this.ingredients.get(this.width - j - 1 + i * this.width);
                } else {
                    ingredient = this.ingredients.get(j + i * this.width);
                }
                ItemStack nmsInputItem = craftingInput.getItem(j, i);
                org.bukkit.inventory.ItemStack bukkitInputItem = CraftItemStack.asCraftMirror(nmsInputItem);
                if (!IngredientUtils.testOptionalChoice(ingredient, bukkitInputItem)) return false;
            }
        }
        return true;
    }
}
