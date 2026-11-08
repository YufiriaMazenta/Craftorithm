package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.nms.NmsRecipeAccessor;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;


    @Override
    public boolean registerRecipe(Recipe recipe, boolean sync2Client) {
        return NmsRecipeAccessor.findImpl().registerRecipe(recipe, sync2Client) == NmsRecipeAccessor.RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        return NmsRecipeAccessor.findImpl().unregisterRecipe(recipeKey, sync2Client);
    }

}
