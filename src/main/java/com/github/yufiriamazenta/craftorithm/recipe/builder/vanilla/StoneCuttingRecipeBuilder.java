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
    public StoneCuttingRecipeBuilder result(ItemStack result) {
        return (StoneCuttingRecipeBuilder) super.result(result);
    }

    @Override
    public StoneCuttingRecipeBuilder key(NamespacedKey key) {
        return (StoneCuttingRecipeBuilder) super.key(key);
    }

    public StonecuttingRecipe build() {
        return new StonecuttingRecipe(getKey(), getResult(), source);
    }

    public static StoneCuttingRecipeBuilder builder() {
        return new StoneCuttingRecipeBuilder();
    }

    public RecipeChoice getSource() {
        return source.clone();
    }

}
