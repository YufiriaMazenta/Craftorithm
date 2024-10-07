package pers.yufiria.craftorithm.recipe.crafting;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.RecipeIngredient;
import pers.yufiria.craftorithm.recipe.RecipeResult;

import java.util.Collections;
import java.util.List;

public class ShapelessRecipe extends CraftingRecipe {

    private List<RecipeIngredient> ingredients;

    public ShapelessRecipe(@NotNull NamespacedKey recipeKey, @NotNull RecipeResult result, int priority, List<RecipeIngredient> ingredients) {
        super(recipeKey, result, priority);
        this.ingredients = ingredients;
    }

    @Override
    public boolean match(CraftInput input) {
        if (input.isEmpty())
            return false;
        return false;
    }

    public List<RecipeIngredient> ingredients() {
        return Collections.unmodifiableList(ingredients);
    }
}
