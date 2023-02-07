package top.oasismc.oasisrecipe.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.item.ItemManager;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;
import top.oasismc.oasisrecipe.script.condition.ConditionDispatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OasisRecipeAPI {

    INSTANCE;

    private final Map<Plugin, List<Recipe>> pluginRecipeMap = new HashMap<>();

    public ActionDispatcher getActionDispatcher() {
        return OasisRecipe.getInstance().getActionDispatcher();
    }

    public ConditionDispatcher getConditionDispatcher() {
        return OasisRecipe.getInstance().getConditionDispatcher();
    }

    public ItemStack getOasisRecipeItem(String itemName) {
        return ItemManager.getOasisRecipeItem(itemName);
    }

    public Map<Plugin, List<Recipe>> getPluginRegRecipeMap() {
        return Collections.unmodifiableMap(pluginRecipeMap);
    }

    public void regRecipes(Plugin plugin, List<Recipe> recipes) {
        pluginRecipeMap.put(plugin, recipes);
    }

}
