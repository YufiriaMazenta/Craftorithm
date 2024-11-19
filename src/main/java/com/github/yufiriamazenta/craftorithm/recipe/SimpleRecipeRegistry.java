package com.github.yufiriamazenta.craftorithm.recipe;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public enum SimpleRecipeRegistry implements RecipeRegistry {

    INSTANCE;
    private final Map<RecipeType, Map<NamespacedKey, Recipe>> registeredRecipeMap = new ConcurrentHashMap<>();

    @Override
    public RegisterResult registerRecipe(@NotNull Recipe recipe) {
        Objects.requireNonNull(recipe);
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        Map<NamespacedKey, Recipe> map;
        if (registeredRecipeMap.containsKey(recipeType)) {
            map = registeredRecipeMap.get(recipeType);
            if (map.containsKey(recipeKey)) {
                return RegisterResult.DUPLICATE;
            }
        } else {
            map = new ConcurrentHashMap<>();
            registeredRecipeMap.put(recipeType, map);
        }
        switch (recipeType) {
            case VANILLA_SHAPED -> {
                Bukkit.getServer().addRecipe(recipe);
                if (CrypticLibBukkit.platform().isPaper()) {
                    if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_1)) {
                        Bukkit.updateRecipes();
                    }
                }
            }
            case UNKNOWN -> {
                return RegisterResult.UNSUPPORTED;
            }
        }
        map.put(recipeKey, recipe);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void resetRecipes() {

    }

    @Override
    public UnregisterResult unregisterRecipe(NamespacedKey recipeKey) {
        return null;
    }

}
