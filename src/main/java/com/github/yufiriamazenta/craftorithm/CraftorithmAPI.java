package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CraftorithmAPI {

    INSTANCE;

    private final Map<Plugin, List<Recipe>> pluginRecipeMap = new HashMap<>();

    public ItemStack getOasisRecipeItem(String itemName) {
        return ItemManager.getCraftorithmItem(itemName);
    }

    public Map<Plugin, List<Recipe>> getPluginRegRecipeMap() {
        return Collections.unmodifiableMap(pluginRecipeMap);
    }

    public void regRecipes(Plugin plugin, List<Recipe> recipes) {
        pluginRecipeMap.put(plugin, recipes);
    }

    public ArcencielDispatcher getArcencielDispatcher() {
        return ArcencielDispatcher.INSTANCE;
    }

    public Map<Recipe, RecipeType> getPluginRecipes() {
        return RecipeManager.getPluginRecipes();
    }

}
