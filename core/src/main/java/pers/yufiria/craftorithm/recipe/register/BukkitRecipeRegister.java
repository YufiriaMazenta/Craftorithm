package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.recipe.RecipeFingerManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.util.ServerUtils;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe) {
        boolean result;
        if (ServerUtils.after1_20Paper()) {
            //1.20.1以上paper端在添加配方时不对玩家进行更新,等加载完毕后统一更新
            result = Bukkit.addRecipe(recipe, false);
        } else {
            result = Bukkit.addRecipe(recipe);
        }
        if (result) {
            registerFingerIfNeeded(recipe);
        }
        return result;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        RecipeFingerManager.INSTANCE.unregisterRecipeFinger(recipeKey);
        if (ServerUtils.after1_20Paper()) {
            //1.20.1以上paper端在删除配方时不对玩家进行更新,等加载完毕后统一更新
            return Bukkit.removeRecipe(recipeKey, false);
        } else {
            return Bukkit.removeRecipe(recipeKey);
        }
    }

    private void registerFingerIfNeeded(Recipe recipe) {
        if (!(recipe instanceof ShapedRecipe) && !(recipe instanceof ShapelessRecipe)) {
            return;
        }
        if (!(recipe instanceof Keyed keyed)) {
            return;
        }
        NamespacedKey recipeKey = keyed.getKey();
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(recipeKey);
        if (config != null) {
            RecipeFingerManager.INSTANCE.registerRecipeFinger(recipeKey, config);
        }
    }

}
