package com.github.yufiriamazenta.craftorithm.recipe.custom;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface CustomRecipe extends Recipe {

    RecipeType getRecipeType();

    NamespacedKey getKey();

}
