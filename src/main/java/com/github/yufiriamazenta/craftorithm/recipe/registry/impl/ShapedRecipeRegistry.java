package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ShapedRecipeRegistry extends CraftingRecipeRegistry {

    private String[] shape;
    private Map<Character, RecipeChoice> recipeChoiceMap;

    public ShapedRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
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
    public ShapedRecipeRegistry setCraftingBookCategory(CraftingBookCategory craftingBookCategory) {
        return (ShapedRecipeRegistry) super.setCraftingBookCategory(craftingBookCategory);
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(result(), "Recipe key cannot be null");
        Objects.requireNonNull(shape, "Recipe shape cannot be null");
        Objects.requireNonNull(recipeChoiceMap, "Recipe ingredients cannot be null");
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

        if (craftingBookCategory != null)
            shapedRecipe.setCategory(craftingBookCategory);

        shapedRecipe.setGroup(group());
        RecipeManager.INSTANCE.regRecipe(group(), shapedRecipe, RecipeType.VANILLA_SHAPED);
    }


}
