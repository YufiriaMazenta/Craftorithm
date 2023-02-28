package me.yufiria.craftorithm.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

public class SmithingRecipeBuilder extends AbstractRecipeBuilder {

    private RecipeChoice base, addition;

    private SmithingRecipeBuilder() {}

    @Override
    public SmithingRecipeBuilder key(NamespacedKey key) {
        return (SmithingRecipeBuilder) super.key(key);
    }

    @Override
    public SmithingRecipeBuilder result(ItemStack result) {
        return (SmithingRecipeBuilder) super.result(result);
    }

    public RecipeChoice getBase() {
        return base;
    }

    public SmithingRecipeBuilder base(RecipeChoice base) {
        this.base = base;
        return this;
    }

    public RecipeChoice getAddition() {
        return addition;
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

}
