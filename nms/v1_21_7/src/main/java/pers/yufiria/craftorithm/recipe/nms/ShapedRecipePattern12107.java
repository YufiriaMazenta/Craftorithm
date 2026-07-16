package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ShapedRecipePattern12107 extends CustomShapedRecipePattern<CraftingInput> {

    public ShapedRecipePattern12107(
        int width,
        int height,
        List<Optional<RecipeChoice>> ingredients
    ) {
        super(width, height, ingredients);
    }

    public static ShapedRecipePattern12107 fromBukkitRecipe(ShapedRecipe shapedRecipe) {
        String[] shape = shapedRecipe.getShape();
        Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
        int height = shape.length;
        int width = 0;
        for (String line : shape) {
            width = Math.max(line.length(), width);
        }
        List<Optional<RecipeChoice>> ingredients = new ArrayList<>(width * height);
        shrink(shape);
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
        return new ShapedRecipePattern12107(width, height, ingredients);
    }

    public boolean matches(CraftingInput craftingInput) {
        if (craftingInput.e() != this.ingredientCount) {
            return false;
        }
        if (craftingInput.f() != width) {
            return false;
        }
        if (craftingInput.g() != height) {
            return false;
        }

        if (!symmetrical && this.matches(craftingInput, true)) {
            return true;
        }
        return this.matches(craftingInput, false);
    }

    public boolean matches(CraftingInput craftingInput, boolean symmetrical) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Optional<RecipeChoice> ingredient;
                if (symmetrical) {
                    ingredient = this.ingredients.get(this.width - j - 1 + i * this.width);
                } else {
                    ingredient = this.ingredients.get(j + i * this.width);
                }

                ItemStack nmsInputItem = craftingInput.a(j, i);
                if (ingredient.isEmpty()) {
                    return true;
                }
                RecipeChoice recipeChoice = ingredient.get();
                org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asBukkitCopy(nmsInputItem);
                if (!recipeChoice.test(bukkitCopy)) {
                    return false;
                }

            }
        }
        return true;
    }

}
