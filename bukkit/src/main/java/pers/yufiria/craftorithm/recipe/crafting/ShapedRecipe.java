package pers.yufiria.craftorithm.recipe.crafting;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.StackedItemId;
import pers.yufiria.craftorithm.recipe.RecipeIngredient;
import pers.yufiria.craftorithm.recipe.RecipeResult;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShapedRecipe extends CraftingRecipe {

    private List<List<RecipeIngredient>> ingredients;
    private Integer ingredientCount;
    private Integer width, height;

    public ShapedRecipe(@NotNull NamespacedKey key, @NotNull RecipeResult result, int priority, List<List<RecipeIngredient>> ingredients) {
        super(key, result, priority);
        this.ingredients = ingredients;
        updateIngredientCountAndSize();
        //补充矩阵中不够长的行,保证列数相等
        for (List<RecipeIngredient> ingredientLine : ingredients) {
            if (ingredientLine.size() < width) {
                for (int i = 0; i < width - ingredientLine.size(); i++) {
                    ingredientLine.add(null);
                }
            }
        }
    }

    @Override
    public boolean match(CraftInput input) {
        if (input.isEmpty()) {
            return false;
        }
        if (!Objects.equals(input.inputCount(), ingredientCount)) {
            return false;
        }
        if (!Objects.equals(input.width(), width)) {
            return false;
        }
        if (!Objects.equals(input.height(), height)) {
            return false;
        }
        boolean match = true;
        //比对原材料
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                RecipeIngredient ingredient = ingredients.get(i).get(j);
                StackedItemId inputItem = input.getInputItem(i, j);
                if (ingredient == null)
                    continue;
                if (!ingredient.match(inputItem)) {
                    match = false;
                    break;
                }
            }
        }
        //比对翻转原材料
        boolean symmetricalMatch = true;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                RecipeIngredient ingredient = ingredients.get(i).get(j);
                StackedItemId inputItem = input.getInputItem(width - 1 - i, height - 1 - j);
                if (ingredient == null)
                    continue;
                if (!ingredient.match(inputItem)) {
                    symmetricalMatch = false;
                    break;
                }
            }
        }
        return match || symmetricalMatch;
    }

    public List<List<RecipeIngredient>> ingredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public Integer ingredientCount() {
        return this.ingredientCount;
    }

    private void updateIngredientCountAndSize() {
        this.ingredientCount = 0;
        this.height = ingredients.size();
        for (List<RecipeIngredient> ingredient : this.ingredients) {
            int tempWidth = 0;
            for (RecipeIngredient recipeIngredient : ingredient) {
                if (recipeIngredient != null) {
                    this.ingredientCount++;
                }
                tempWidth ++;
            }
            if (this.width == null || tempWidth > this.width) {
                this.width = tempWidth;
            }
        }
    }

}
