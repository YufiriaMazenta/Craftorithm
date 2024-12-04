package com.github.yufiriamazenta.craftorithm.recipe.loader.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.CookingRecipe;
import org.jetbrains.annotations.Nullable;

public enum SmeltingRecipeLoader implements RecipeLoader<CookingRecipe<?>> {

    INSTANCE;

    @Override
    public @Nullable CookingRecipe<?> loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        return null;
    }

}
