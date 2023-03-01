package me.yufiria.craftorithm.recipe.custom;

import me.yufiria.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface CustomRecipe extends Recipe {

    RecipeType getRecipeType();

    NamespacedKey getKey();

    void setKey(NamespacedKey key);

}
