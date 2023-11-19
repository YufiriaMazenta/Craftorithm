package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class PotionMixBuilder extends AbstractRecipeBuilder {

    private RecipeChoice input, ingredient;

    private PotionMixBuilder() {
        super();
    }

    @Override
    public PotionMixRecipe build() {
        if (key() == null)
            throw new IllegalArgumentException("Recipe key cannot be null");
        if (result() == null)
            throw new IllegalArgumentException("Recipe result cannot be null");
        if (ingredient == null)
            throw new IllegalArgumentException("Recipe ingredient cannot be null");
        if (input == null)
            throw new IllegalArgumentException("Recipe input cannot be null");
        return new PotionMixRecipe(new PotionMix(key(), result(), input, ingredient));
    }

    public RecipeChoice input() {
        return input;
    }

    public PotionMixBuilder setInput(RecipeChoice input) {
        this.input = input;
        return this;
    }

    @Override
    public PotionMixBuilder setKey(NamespacedKey key) {
        return (PotionMixBuilder) super.setKey(key);
    }

    @Override
    public PotionMixBuilder setResult(ItemStack result) {
        return (PotionMixBuilder) super.setResult(result);
    }

    public RecipeChoice ingredient() {
        return ingredient;
    }

    public PotionMixBuilder setIngredient(RecipeChoice ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public static PotionMixBuilder builder() {
        return new PotionMixBuilder();
    }

}
