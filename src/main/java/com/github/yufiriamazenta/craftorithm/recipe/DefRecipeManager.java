package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.recipe.custom.CustomRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DefRecipeManager {

    INSTANCE;
    private final Map<RecipeType, Map<String, List<NamespacedKey>>> craftorithmRecipeMap;

    DefRecipeManager() {
        craftorithmRecipeMap = new ConcurrentHashMap<>();
    }

    public void regRecipe(String recipeName, Recipe recipe) {

    }

    public void regShapedRecipe(String recipeName, ShapedRecipe recipe) {
        RecipeType recipeType = RecipeType.SHAPED;
        if (!craftorithmRecipeMap.containsKey(recipeType))
            craftorithmRecipeMap.put(recipeType, new ConcurrentHashMap<>());
        Map<String, List<NamespacedKey>> recipeMap = craftorithmRecipeMap.get(recipeType);
        if (!recipeMap.containsKey(recipeName))
            recipeMap.put(recipeName, new ArrayList<>());
        List<NamespacedKey> recipeGroup = recipeMap.get(recipeName);
        recipeGroup.add(recipe.getKey());
        Bukkit.addRecipe(recipe);
    }

    public void regShapelessRecipe(String recipeName, ShapelessRecipe recipe) {
        RecipeType recipeType = RecipeType.SHAPELESS;
        if (!craftorithmRecipeMap.containsKey(recipeType))
            craftorithmRecipeMap.put(recipeType, new ConcurrentHashMap<>());
        Map<String, List<NamespacedKey>> recipeMap = craftorithmRecipeMap.get(recipeType);
        if (!recipeMap.containsKey(recipeName))
            recipeMap.put(recipeName, new ArrayList<>());
        List<NamespacedKey> recipeGroup = recipeMap.get(recipeName);
        recipeGroup.add(recipe.getKey());
        Bukkit.addRecipe(recipe);
    }

    public void regCookingRecipe(String recipeName, CookingRecipe<?> recipe) {
        RecipeType recipeType = RecipeType.COOKING;
        if (!craftorithmRecipeMap.containsKey(recipeType))
            craftorithmRecipeMap.put(recipeType, new ConcurrentHashMap<>());
        Map<String, List<NamespacedKey>> recipeMap = craftorithmRecipeMap.get(recipeType);
        if (!recipeMap.containsKey(recipeName))
            recipeMap.put(recipeName, new ArrayList<>());
        List<NamespacedKey> recipeGroup = recipeMap.get(recipeName);
        recipeGroup.add(recipe.getKey());
        Bukkit.addRecipe(recipe);
    }

    public Map<RecipeType, Map<String, List<NamespacedKey>>> recipeMap() {
        return craftorithmRecipeMap;
    }

    public Recipe getRecipe(NamespacedKey namespacedKey) {
        //TODO
        return null;
    }

    public NamespacedKey getRecipeKey(Recipe recipe) {
        if (recipe == null)
            return null;
        if (recipe instanceof CustomRecipe) {
            return ((CustomRecipe) recipe).key();
        }
        return ((Keyed) recipe).getKey();
    }


}
