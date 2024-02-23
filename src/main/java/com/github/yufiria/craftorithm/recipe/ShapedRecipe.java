package com.github.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;

public class ShapedRecipe extends BaseRecipe<String[][]> {

    protected String[][] ingredients;

    public ShapedRecipe(NamespacedKey recipeKey) {
        super(recipeKey);
        this.priority = 0;
    }

    public ShapedRecipe(NamespacedKey recipeKey, String result, String[][] ingredients, Integer priority) {
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

    @Override
    public boolean match(String[][] matchObj) {
        //TODO
        return false;
    }

}
