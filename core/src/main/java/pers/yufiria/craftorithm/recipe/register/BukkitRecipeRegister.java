package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.util.ServerUtils;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe) {
        if (ServerUtils.after1_20Paper()) {
            //1.20.1以上paper端在添加配方时不对玩家进行更新,等加载完毕后统一更新
            return Bukkit.addRecipe(recipe, false);
        } else {
            return Bukkit.addRecipe(recipe);
        }
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        if (ServerUtils.after1_20Paper()) {
            //1.20.1以上paper端在删除配方时不对玩家进行更新,等加载完毕后统一更新
            return Bukkit.removeRecipe(recipeKey, false);
        } else {
            return Bukkit.removeRecipe(recipeKey);
        }
    }

}
