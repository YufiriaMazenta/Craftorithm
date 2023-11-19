package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

public class StoneCuttingRecipeBuilder extends AbstractRecipeBuilder {

    private RecipeChoice source;

    private StoneCuttingRecipeBuilder() {}

    public StoneCuttingRecipeBuilder source(RecipeChoice source) {
        this.source = source;
        return this;
    }

    @Override
    public StoneCuttingRecipeBuilder setResult(ItemStack result) {
        return (StoneCuttingRecipeBuilder) super.setResult(result);
    }

    @Override
    public StoneCuttingRecipeBuilder setKey(NamespacedKey key) {
        return (StoneCuttingRecipeBuilder) super.setKey(key);
    }

    public StonecuttingRecipe build() {
        if (key() == null) {
            throw new IllegalArgumentException("Recipe key cannot be null");
        }
        if (result() == null) {
            throw new IllegalArgumentException("Recipe result cannot be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("Recipe input cannot be null");
        }
        return new StonecuttingRecipe(key(), result(), source);
    }

    public static StoneCuttingRecipeBuilder builder() {
        return new StoneCuttingRecipeBuilder();
    }

    public RecipeChoice getSource() {
        return source.clone();
    }

}
