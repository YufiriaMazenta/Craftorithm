package com.github.yufiriamazenta.craftorithm.util;

import crypticlib.CrypticLibBukkit;
import crypticlib.platform.Platform;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

public class RecipeUtil {

    public static void regRecipe(Recipe recipe) {
        if (CrypticLibBukkit.platform().type().equals(Platform.PlatformType.BUKKIT)) {
            //当不是Paper端时，使用Bukkit的注册方法
            Bukkit.addRecipe(recipe);
        } else {
            //当是Paper及其衍生端时，使用会刷新配方的方法注册
            Bukkit.addRecipe(recipe, true);
        }
    }

}
