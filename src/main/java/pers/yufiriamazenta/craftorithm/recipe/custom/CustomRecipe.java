package pers.yufiriamazenta.craftorithm.recipe.custom;

import pers.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface CustomRecipe extends Recipe {

    RecipeType recipeType();

    NamespacedKey key();

}
