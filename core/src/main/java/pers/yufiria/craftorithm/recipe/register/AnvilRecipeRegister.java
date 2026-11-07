package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipeHandler;

public enum AnvilRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe, boolean sync2Client) {
        if (recipe instanceof AnvilRecipe anvilRecipe) {
            return AnvilRecipeHandler.INSTANCE.registerAnvilRecipe(anvilRecipe);
        }
        return false;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        return AnvilRecipeHandler.INSTANCE.unregisterAnvilRecipe(recipeKey);
    }

}
