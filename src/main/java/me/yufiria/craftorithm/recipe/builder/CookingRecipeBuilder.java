package me.yufiria.craftorithm.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.Locale;

public class CookingRecipeBuilder extends AbstractRecipeBuilder {

    private RecipeChoice source;
    private int exp, time;
    private CookingBlock cookingBlock;

    private CookingRecipeBuilder() {
        this.exp = 0;
        this.time = 0;
        this.cookingBlock = CookingBlock.FURNACE;
    }

    @Override
    public CookingRecipeBuilder result(ItemStack result) {
        return (CookingRecipeBuilder) super.result(result);
    }

    @Override
    public CookingRecipeBuilder key(NamespacedKey key) {
        return (CookingRecipeBuilder) super.key(key);
    }

    public CookingRecipeBuilder exp(int exp) {
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
        return source;
    }

    public int getExp() {
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
                cookingRecipe = new FurnaceRecipe(getKey(), getResult(), source, exp, time);
                break;
            case SMOKER:
                cookingRecipe = new SmokingRecipe(getKey(), getResult(), source, exp, time);
                break;
            case BLAST:
                cookingRecipe = new BlastingRecipe(getKey(), getResult(), source, exp, time);
                break;
            case CAMPFIRE:
                cookingRecipe = new CampfireRecipe(getKey(), getResult(), source, exp, time);
                break;
        }
        return cookingRecipe;
    }

    public static CookingRecipeBuilder builder() {
        return new CookingRecipeBuilder();
    }

    enum CookingBlock {
        FURNACE,
        BLAST,
        SMOKER,
        CAMPFIRE
    }

}
