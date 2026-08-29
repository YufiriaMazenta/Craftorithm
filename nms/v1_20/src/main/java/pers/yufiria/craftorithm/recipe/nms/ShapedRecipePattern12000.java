package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ShapedRecipePattern12000 extends CustomShapedRecipePattern<InventoryCrafting> {

    ShapedRecipePattern12000(
        int width,
        int height,
        List<Optional<RecipeChoice>> ingredients
    ) {
        super(width, height, ingredients);
    }

    @Override
    public boolean matches(InventoryCrafting craftingInput) {
        for(int i = 0; i <= craftingInput.f() - this.width; ++i) {
            for(int j = 0; j <= craftingInput.g() - this.height; ++j) {
                if (this.matches(craftingInput, i, j, true)) {
                    return true;
                }

                if (this.matches(craftingInput, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(InventoryCrafting craftingInput, int i, int j, boolean flag) {
        for(int k = 0; k < craftingInput.f(); ++k) {
            for(int l = 0; l < craftingInput.g(); ++l) {
                int i1 = k - i;
                int j1 = l - j;
                Optional<RecipeChoice> ingredient = Optional.empty();
                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (flag) {
                        ingredient = this.ingredients.get(this.width - i1 - 1 + j1 * this.width);
                    } else {
                        ingredient = this.ingredients.get(i1 + j1 * this.width);
                    }
                }

                ItemStack nmsInput = craftingInput.a(k + l * craftingInput.f());
                if (!IngredientUtils.testOptionalChoice(ingredient, CraftItemStack.asCraftMirror(nmsInput))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected boolean matches(InventoryCrafting craftingInput, boolean symmetrical) {
        //1.20的匹配不靠这个方法
        return false;
    }

    public static ShapedRecipePattern12000 fromBukkitRecipe(ShapedRecipe shapedRecipe) {
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
        return new ShapedRecipePattern12000(width, height, ingredients);
    }


}
