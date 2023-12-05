package com.github.yufiriamazenta.craftorithm.recipe.builder.custom;

import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

public class AnvilRecipeBuilder extends AbstractRecipeBuilder {

    private RecipeChoice base, addition;

    private AnvilRecipeBuilder() {}

    @Override
    public Recipe build() {
        if (key() == null)
            throw new IllegalArgumentException("Recipe key cannot be null");
        if (result() == null)
            throw new IllegalArgumentException("Recipe result cannot be null");
        if (base == null)
            throw new IllegalArgumentException("Recipe base cannot be null");
        if (addition == null)
            throw new IllegalArgumentException("Recipe addition cannot be null");
        return new AnvilRecipe(key(), result(), base, addition);
    }

    public RecipeChoice base() {
        return base;
    }

    public AnvilRecipeBuilder setBase(RecipeChoice base) {
        this.base = base;
        return this;
    }

    public RecipeChoice addition() {
        return addition;
    }

    public AnvilRecipeBuilder setAddition(RecipeChoice addition) {
        this.addition = addition;
        return this;
    }

    public static AnvilRecipeBuilder builder() {
        return new AnvilRecipeBuilder();
    }

}
