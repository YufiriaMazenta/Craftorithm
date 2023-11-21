package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

/**
 * 1.20以上版本的锻造台配方Builder
 */
public class XSmithingRecipeBuilder extends SmithingRecipeBuilder {

    private RecipeChoice template;
    private final SmithingType type;

    protected XSmithingRecipeBuilder() {
        type = SmithingType.DEFAULT;
    }

    protected XSmithingRecipeBuilder(SmithingType type) {
        this.type =  type;
    }

    @Override
    public XSmithingRecipeBuilder setKey(NamespacedKey key) {
        return (XSmithingRecipeBuilder) super.setKey(key);
    }

    @Override
    public XSmithingRecipeBuilder setResult(ItemStack result) {
        return (XSmithingRecipeBuilder) super.setResult(result);
    }

    @Override
    public XSmithingRecipeBuilder setBase(RecipeChoice base) {
        this.base = base;
        return this;
    }

    @Override
    public XSmithingRecipeBuilder setAddition(RecipeChoice addition) {
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
        switch (type) {
            case DEFAULT:
            default:
                return new SmithingRecipe(key(), result(), base, addition);
            case TRIM:
                return new SmithingTrimRecipe(key(), template, base, addition);
            case TRANSFORM:
                return new SmithingTransformRecipe(key(), result(), template, base, addition);
        }
    }

    public static XSmithingRecipeBuilder builder() {
        return new XSmithingRecipeBuilder();
    }

    public static XSmithingRecipeBuilder builder(SmithingType type) {
        return new XSmithingRecipeBuilder(type);
    }

    public XSmithingRecipeBuilder setTemplate(RecipeChoice template) {
        this.template = template;
        return this;
    }

    public RecipeChoice template() {
        return template.clone();
    }

    public SmithingType type() {
        return type;
    }

    public enum SmithingType {
        TRIM, TRANSFORM, DEFAULT
    }

}
