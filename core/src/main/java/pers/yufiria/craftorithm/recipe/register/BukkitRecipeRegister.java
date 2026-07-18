package pers.yufiria.craftorithm.recipe.register;

import crypticlib.CrypticLibBukkit;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeRegister;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;


    @Override
    public boolean registerRecipe(Recipe recipe, boolean updateRecipes) {
        if (PluginConfigs.USE_EXPERIMENTAL_RECIPE_REGISTER.value()) {
            return CraftorithmRecipeRegistry.findImpl().registerRecipe(recipe, updateRecipes) == CraftorithmRecipeRegistry.RegisterResult.SUCCESS;
        }

        if (CrypticLibBukkit.isPaper()) {
            //其实这里的调用根本没用，paper去掉了这个参数的作用，自我安慰罢了
            return Bukkit.addRecipe(recipe, updateRecipes);
        } else {
            return Bukkit.addRecipe(recipe);
        }
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        if (PluginConfigs.USE_EXPERIMENTAL_RECIPE_REGISTER.value()) {
            return CraftorithmRecipeRegistry.findImpl().unregisterRecipe(recipeKey, updateRecipes);
        }

        if (CrypticLibBukkit.isPaper()) {
            //其实这里的调用根本没用，paper去掉了这个参数的作用，自我安慰罢了
            return Bukkit.removeRecipe(recipeKey, updateRecipes);
        } else {
            return Bukkit.removeRecipe(recipeKey);
        }
    }

}
