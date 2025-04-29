package com.github.yufiriamazenta.craftorithm.util;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

public class RecipeUtils {

    public static boolean addBukkitRecipe(Recipe recipe) {
        if (CrypticLibBukkit.isPaper() && MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_1)) {
            //1.20.1以上paper端在添加配方时不对玩家进行更新,等加载完毕后统一更新
            return Bukkit.addRecipe(recipe, false);
        } else {
            return Bukkit.addRecipe(recipe);
        }
    }

}
