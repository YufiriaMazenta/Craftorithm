package pers.yufiriamazenta.craftorithm.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

public class RecipeUtil {

    public static void regVanillaRecipe(Recipe recipe) {
        Bukkit.addRecipe(recipe);
    }

}
