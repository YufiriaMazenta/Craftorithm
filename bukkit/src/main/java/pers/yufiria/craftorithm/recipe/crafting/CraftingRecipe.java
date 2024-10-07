package pers.yufiria.craftorithm.recipe.crafting;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.Recipe;
import pers.yufiria.craftorithm.recipe.RecipeResult;

public abstract class CraftingRecipe implements Recipe {

    protected final @NotNull NamespacedKey recipeKey;
    protected @NotNull RecipeResult result;
    protected final int priority;

    protected CraftingRecipe(@NotNull NamespacedKey recipeKey, @NotNull RecipeResult result, int priority) {
        this.recipeKey = recipeKey;
        this.priority = priority;
        this.result = result;
    }

    public abstract boolean match(CraftInput input);

    @Override
    public @NotNull NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public @NotNull RecipeResult result() {
        return result;
    }

    public CraftingRecipe setResult(@NotNull RecipeResult result) {
        this.result = result;
        return this;
    }

    @Override
    public int priority() {
        return priority;
    }

}
