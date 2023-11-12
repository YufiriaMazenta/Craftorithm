package com.github.yufiriamazenta.craftorithm.recipe.custom;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AnvilRecipe implements CustomRecipe {

    private AnvilRecipeItem base, addition;
    private ItemStack result;
    private NamespacedKey namespacedKey;
    private int costLevel;

    public AnvilRecipe(NamespacedKey namespacedKey, ItemStack result, AnvilRecipeItem base, AnvilRecipeItem addition, int costLevel) {
        this.base = base;
        this.addition = addition;
        this.result = result;
        this.namespacedKey = namespacedKey;
        this.costLevel = costLevel;
    }

    public AnvilRecipe(NamespacedKey namespacedKey, ItemStack result) {
        this(namespacedKey, result, null, null, 0);
    }

    public AnvilRecipeItem getBase() {
        return base;
    }

    public void setBase(AnvilRecipeItem base) {
        this.base = base;
    }

    public AnvilRecipeItem getAddition() {
        return addition;
    }

    public void setAddition(AnvilRecipeItem addition) {
        this.addition = addition;
    }

    @Override
    public @NotNull ItemStack getResult() {
        return this.result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }

    @Override
    public void setKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public int getCostLevel() {
        return costLevel;
    }

    public void setCostLevel(int costLevel) {
        this.costLevel = costLevel;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.ANVIL;
    }

    /**
     * 检测两个物品是否符合此配方需求的物品
     * @param base 左侧物品
     * @param addition 右侧物品
     * @return 符合则返回true，反之false
     */
    public boolean checkSource(ItemStack base, ItemStack addition) {
        return this.base.check(base) && this.addition.check(addition);
    }

}
