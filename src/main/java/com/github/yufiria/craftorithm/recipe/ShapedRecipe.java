package com.github.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShapedRecipe extends BaseRecipe<String[][]> {

    protected ItemStack result;
    protected String[][] ingredients;

    public ShapedRecipe(NamespacedKey recipeKey) {
        super(recipeKey);
        this.priority = 0;
    }

    public ShapedRecipe(NamespacedKey recipeKey, ItemStack result, String[][] ingredients, Integer priority) {
        super(recipeKey);
        this.result = result;
        this.ingredients = ingredients;
        this.priority = priority;
    }

    public String[][] ingredients() {
        return ingredients;
    }

    public ShapedRecipe setIngredients(String[][] ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public ShapedRecipe setResult(ItemStack result) {
        this.result = result;
        return this;
    }

    @Override
    @NotNull
    public ItemStack result() {
        return result;
    }

    @Override
    public boolean match(String[][] matchObj) {
        //TODO
        return false;
    }

}
