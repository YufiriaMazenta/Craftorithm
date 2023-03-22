package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

public class SmithingRecipeBuilder extends AbstractRecipeBuilder {

    private RecipeChoice base, addition, template;
    private final SmithingType type;

    protected SmithingRecipeBuilder() {
        type = SmithingType.DEFAULT;
    }

    protected SmithingRecipeBuilder(SmithingType type) {
        this.type =  type;
    }

    @Override
    public SmithingRecipeBuilder key(NamespacedKey key) {
        return (SmithingRecipeBuilder) super.key(key);
    }

    @Override
    public SmithingRecipeBuilder result(ItemStack result) {
        return (SmithingRecipeBuilder) super.result(result);
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

    public SmithingRecipe build() {
        switch (type) {
            case DEFAULT:
            default:
                return new SmithingRecipe(getKey(), getResult(), base, addition);
            case TRIM:
                return new SmithingTrimRecipe(getKey(), template, base, addition);
            case TRANSFORM:
                return new SmithingTransformRecipe(getKey(), getResult(), template, base, addition);
        }

    }

    public static SmithingRecipeBuilder builder() {
        return new SmithingRecipeBuilder();
    }

    @Deprecated
    public static SmithingRecipeBuilder builder(SmithingType type) {
        return new SmithingRecipeBuilder(type);
    }

    public SmithingRecipeBuilder template(RecipeChoice template) {
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
