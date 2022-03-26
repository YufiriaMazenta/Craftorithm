package top.oasismc.oasisrecipe.api;

import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRegistrar {

    private static final Map<Plugin, List<Recipe>> pluginRecipeMap = new HashMap<>();

    public static Map<Plugin, List<Recipe>> getPluginRecipeMap() {
        return pluginRecipeMap;
    }

    public static void regRecipes(Plugin plugin, List<Recipe> recipes) {
        pluginRecipeMap.put(plugin, recipes);
    }

}
