package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;


    @Override
    public boolean registerRecipe(Recipe recipe, boolean sync2Client) {
        return CraftorithmRecipeRegistry.findImpl().registerRecipe(recipe, sync2Client) == CraftorithmRecipeRegistry.RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        return CraftorithmRecipeRegistry.findImpl().unregisterRecipe(recipeKey, sync2Client);
    }

}
