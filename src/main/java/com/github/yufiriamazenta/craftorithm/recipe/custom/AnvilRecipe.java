package com.github.yufiriamazenta.craftorithm.recipe.custom;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public class AnvilRecipe implements CustomRecipe {

    private NamespacedKey key;
    private ItemStack result;
    private RecipeChoice base, addition;

    public AnvilRecipe(NamespacedKey key, ItemStack result, RecipeChoice base, RecipeChoice addition) {
        this.key = key;
        this.result = result;
        this.base = base;
        this.addition = addition;
    }

    public AnvilRecipe setKey(NamespacedKey key) {
        this.key = key;
        return this;
    }

    public AnvilRecipe setResult(ItemStack result) {
        this.result = result;
        return this;
    }

    public RecipeChoice base() {
        return base;
    }

    public AnvilRecipe setBase(RecipeChoice base) {
        this.base = base;
        return this;
    }

    public RecipeChoice addition() {
        return addition;
    }

    public AnvilRecipe setAddition(RecipeChoice addition) {
        this.addition = addition;
        return this;
    }

    @Override
    public RecipeType recipeType() {
        return RecipeType.ANVIL;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public @NotNull ItemStack getResult() {
        return result;
    }

}
