package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import org.bukkit.inventory.ShapedRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShapedRecipeRegistry extends RecipeRegistry {

    private String[] shape;
    private Map<Character, RecipeChoice> recipeChoiceMap;

    public ShapedRecipeRegistry() {
        super();
    }

    public ShapedRecipeRegistry(NamespacedKey namespacedKey, ItemStack result) {
        super(namespacedKey, result);
    }

    public String[] shape() {
        return shape;
    }

    public ShapedRecipeRegistry setShape(String[] shape) {
        this.shape = shape;
        return this;
    }

    public Map<Character, RecipeChoice> recipeChoiceMap() {
        return recipeChoiceMap;
    }

    public ShapedRecipeRegistry setRecipeChoiceMap(Map<Character, RecipeChoice> recipeChoiceMap) {
        this.recipeChoiceMap = recipeChoiceMap;
        return this;
    }

    @Override
    protected void register() {
        if (namespacedKey() == null) {
            throw new IllegalArgumentException("Recipe key cannot be null");
        }
        if (result() == null) {
            throw new IllegalArgumentException("Recipe result cannot be null");
        }
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey(), result());
        shapedRecipe.shape(shape);
        Set<Character> shapeStrChars = new HashSet<>();
        for (String s : shape) {
            for (char c : s.toCharArray()) {
                shapeStrChars.add(c);
            }
        }
        Set<Character> keySet = new HashSet<>(recipeChoiceMap.keySet());
        keySet.removeIf((character -> !shapeStrChars.contains(character)));
        for (Character ingredientKey : keySet) {
            shapedRecipe.setIngredient(ingredientKey, recipeChoiceMap.get(ingredientKey));
        }
        //TODO 通过RecipeManager注册配方
    }


}
