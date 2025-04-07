package pers.yufiria.craftorithm.recipe.register;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.recipe.RecipeRegister;

public enum BukkitRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe) {
        if (CrypticLibBukkit.isPaper() && MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_1)) {
            //1.20.1以上paper端在添加配方时不对玩家进行更新,等加载完毕后统一更新
            return Bukkit.addRecipe(recipe, false);
        } else {
            return Bukkit.addRecipe(recipe);
        }
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        return Bukkit.removeRecipe(recipeKey);
    }

}
