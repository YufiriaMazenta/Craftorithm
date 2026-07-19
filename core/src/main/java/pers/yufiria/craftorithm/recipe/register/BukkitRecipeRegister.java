package pers.yufiria.craftorithm.recipe.register;

import crypticlib.CrypticLibBukkit;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeRegister;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;


    @Override
    public boolean registerRecipe(Recipe recipe, boolean updateRecipes) {
        return CraftorithmRecipeRegistry.findImpl().registerRecipe(recipe, updateRecipes) == CraftorithmRecipeRegistry.RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        return CraftorithmRecipeRegistry.findImpl().unregisterRecipe(recipeKey, updateRecipes);
    }

}
