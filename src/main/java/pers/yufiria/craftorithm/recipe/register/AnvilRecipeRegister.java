package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.recipe.extra.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.anvil.AnvilRecipeHandler;

public enum AnvilRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe) {
        if (recipe instanceof AnvilRecipe anvilRecipe) {
            return AnvilRecipeHandler.INSTANCE.registerAnvilRecipe(anvilRecipe);
        }
        return false;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        return AnvilRecipeHandler.INSTANCE.unregisterAnvilRecipe(recipeKey);
    }

}
