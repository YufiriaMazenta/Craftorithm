package top.oasismc.oasisrecipe.api;

import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

@Deprecated
public class RecipeRegistrar {

    public static Map<Plugin, List<Recipe>> getPluginRecipeMap() {
        return OasisRecipeAPI.INSTANCE.getPluginRecipeMap();
    }

    public static void regRecipes(Plugin plugin, List<Recipe> recipes) {
        OasisRecipeAPI.INSTANCE.regRecipes(plugin, recipes);
    }

}
