package com.github.yufiriamazenta.craftorithm.recipe.builder.custom;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipeItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class AnvilRecipeBuilder extends AbstractRecipeBuilder {

    private AnvilRecipeItem base, addition;
    private ItemStack result;
    private NamespacedKey namespacedKey;
    private int costLevel;

    private AnvilRecipeBuilder() {
        this.costLevel = 0;
    }

    public AnvilRecipeItem getBase() {
        return base;
    }

    public AnvilRecipeBuilder base(AnvilRecipeItem base) {
        this.base = base;
        return this;
    }

    public AnvilRecipeItem getAddition() {
        return addition;
    }

    public AnvilRecipeBuilder addition(AnvilRecipeItem addition) {
        this.addition = addition;
        return this;
    }

    public AnvilRecipeBuilder result(ItemStack result) {
        return (AnvilRecipeBuilder) super.result(result);
    }

    public AnvilRecipeBuilder key(NamespacedKey namespacedKey) {
        return (AnvilRecipeBuilder) super.key(namespacedKey);
    }

    public int getCostLevel() {
        return costLevel;
    }

    public AnvilRecipeBuilder costLevel(int costLevel) {
        this.costLevel = costLevel;
        return this;
    }

    public AnvilRecipe build() {
        return new AnvilRecipe(getKey(), getResult(), getBase(), getAddition(), getCostLevel());
    }

    public static AnvilRecipeBuilder builder() {
        return new AnvilRecipeBuilder();
    }

}
