package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

public class SmithingRecipeBuilder extends AbstractRecipeBuilder {

    protected RecipeChoice base, addition;

    @Override
    public SmithingRecipeBuilder setKey(NamespacedKey key) {
        return (SmithingRecipeBuilder) super.setKey(key);
    }

    @Override
    public SmithingRecipeBuilder setResult(ItemStack result) {
        return (SmithingRecipeBuilder) super.setResult(result);
    }

    public RecipeChoice getBase() {
        return base.clone();
    }

    public SmithingRecipeBuilder base(RecipeChoice base) {
        this.base = base;
        return this;
    }

    public RecipeChoice getAddition() {
        return addition.clone();
    }

    public SmithingRecipeBuilder addition(RecipeChoice addition) {
        this.addition = addition;
        return this;
    }

    @Override
    public SmithingRecipe build() {
        if (key() == null) {
            throw new IllegalArgumentException("Recipe key cannot be null");
        }
        if (result() == null) {
            throw new IllegalArgumentException("Recipe result cannot be null");
        }
        if (base == null) {
            throw new IllegalArgumentException("Recipe base cannot be null");
        }
        if (addition == null) {
            throw new IllegalArgumentException("Recipe addition cannot be null");
        }
        return new SmithingRecipe(key(), result(), base, addition);
    }

    public static SmithingRecipeBuilder builder() {
        return new SmithingRecipeBuilder();
    }

}
