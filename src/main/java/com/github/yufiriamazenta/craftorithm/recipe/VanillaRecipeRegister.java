package com.github.yufiriamazenta.craftorithm.recipe;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public enum VanillaRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe) {
        return Bukkit.addRecipe(recipe);
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        return Bukkit.removeRecipe(recipeKey);
    }
}
