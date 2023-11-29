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

    public CookingRecipeBuilder setExp(float exp) {
        this.exp = exp;
        return this;
    }

    public CookingRecipeBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public CookingRecipeBuilder setBlock(String block) {
        try {
            this.cookingBlock = CookingBlock.valueOf(block.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            this.cookingBlock = CookingBlock.FURNACE;
        }
        return this;
    }

    public CookingRecipeBuilder setBlock(CookingBlock block) {
        this.cookingBlock = block;
        return this;
    }

    public CookingRecipeBuilder setSource(RecipeChoice source) {
        this.source = source;
        return this;
    }

    public RecipeChoice source() {
        return source.clone();
    }

    public float exp() {
        return exp;
    }

    public int getTime() {
        return time;
    }

    public CookingBlock cookingBlock() {
        return cookingBlock;
    }

    public CookingRecipe<?> build() {
        if (key() == null) {
            throw new IllegalArgumentException("Recipe key cannot be null");
        }
        if (result() == null) {
            throw new IllegalArgumentException("Recipe result cannot be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("Recipe source cannot be null");
        }
        CookingRecipe<?> cookingRecipe;
        switch (cookingBlock) {
            case FURNACE:
            default:
                cookingRecipe = new FurnaceRecipe(key(), result(), source, exp, time);
                break;
            case SMOKER:
                cookingRecipe = new SmokingRecipe(key(), result(), source, exp, time);
                break;
            case BLAST_FURNACE:
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
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE
    }

}
