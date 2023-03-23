package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

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
        return new SmithingRecipe(getKey(), getResult(), base, addition);
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
