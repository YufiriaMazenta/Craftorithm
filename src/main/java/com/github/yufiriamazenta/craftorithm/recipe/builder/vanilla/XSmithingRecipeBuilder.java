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
    public XSmithingRecipeBuilder key(NamespacedKey key) {
        return (XSmithingRecipeBuilder) super.key(key);
    }

    @Override
    public XSmithingRecipeBuilder result(ItemStack result) {
        return (XSmithingRecipeBuilder) super.result(result);
    }

    public RecipeChoice getBase() {
        return base.clone();
    }

    @Override
    public XSmithingRecipeBuilder base(RecipeChoice base) {
        this.base = base;
        return this;
    }

    @Override
    public RecipeChoice getAddition() {
        return addition.clone();
    }

    @Override
    public XSmithingRecipeBuilder addition(RecipeChoice addition) {
        this.addition = addition;
        return this;
    }

    @Override
    public SmithingRecipe build() {
        switch (type) {
            case DEFAULT:
            default:
                return new SmithingRecipe(getKey(), getResult(), base, addition);
            case TRIM:
                return new SmithingTrimRecipe(getKey(),template, base, addition);
            case TRANSFORM:
                return new SmithingTransformRecipe(getKey(), getResult(), template, base, addition);
        }
    }

    public static XSmithingRecipeBuilder builder() {
        return new XSmithingRecipeBuilder();
    }

    public static XSmithingRecipeBuilder builder(SmithingType type) {
        return new XSmithingRecipeBuilder(type);
    }

    public XSmithingRecipeBuilder template(RecipeChoice template) {
        this.template = template;
        return this;
    }

    public RecipeChoice getTemplate() {
        return template.clone();
    }

    public SmithingType getType() {
        return type;
    }

    public enum SmithingType {
        TRIM, TRANSFORM, DEFAULT
    }

}
