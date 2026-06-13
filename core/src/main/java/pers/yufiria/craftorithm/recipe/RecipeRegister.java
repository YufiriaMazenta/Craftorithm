package pers.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface RecipeRegister {

    boolean registerRecipe(Recipe recipe);

    boolean unregisterRecipe(NamespacedKey recipeKey);

}
