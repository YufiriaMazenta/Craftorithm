package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.Locale;

public class CookingRecipeBuilder extends AbstractRecipeBuilder {

    private RecipeChoice source;
    private int time;
    private float exp;
    private CookingBlock cookingBlock;

    private CookingRecipeBuilder() {
        this.exp = 0;
        this.time = 200;
        this.cookingBlock = CookingBlock.FURNACE;
    }

    @Override
    public CookingRecipeBuilder setResult(ItemStack result) {
        return (CookingRecipeBuilder) super.setResult(result);
    }

    @Override
    public CookingRecipeBuilder setKey(NamespacedKey key) {
        return (CookingRecipeBuilder) super.setKey(key);
    }

    public CookingRecipeBuilder exp(float exp) {
        this.exp = exp;
        return this;
    }

    public CookingRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    public CookingRecipeBuilder block(String block) {
        try {
            this.cookingBlock = CookingBlock.valueOf(block.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            this.cookingBlock = CookingBlock.FURNACE;
        }
        return this;
    }

    public CookingRecipeBuilder block(CookingBlock block) {
        this.cookingBlock = block;
        return this;
    }

    public CookingRecipeBuilder source(RecipeChoice source) {
        this.source = source;
        return this;
    }

    public RecipeChoice getSource() {
        return source.clone();
    }

    public float getExp() {
        return exp;
    }

    public int getTime() {
        return time;
    }

    public CookingBlock getCookingBlock() {
        return cookingBlock;
    }

    public CookingRecipe<?> build() {
        CookingRecipe<?> cookingRecipe;
        switch (cookingBlock) {
            case FURNACE:
            default:
                cookingRecipe = new FurnaceRecipe(key(), result(), source, exp, time);
                break;
            case SMOKER:
                cookingRecipe = new SmokingRecipe(key(), result(), source, exp, time);
                break;
            case BLAST:
                cookingRecipe = new BlastingRecipe(key(), result(), source, exp, time);
                break;
            case CAMPFIRE:
                cookingRecipe = new CampfireRecipe(key(), result(), source, exp, time);
                break;
        }
        return cookingRecipe;
    }

    public static CookingRecipeBuilder builder() {
        return new CookingRecipeBuilder();
    }

    public enum CookingBlock {
        FURNACE,
        BLAST,
        SMOKER,
        CAMPFIRE
    }

}
