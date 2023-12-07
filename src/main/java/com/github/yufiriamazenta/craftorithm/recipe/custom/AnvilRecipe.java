package com.github.yufiriamazenta.craftorithm.recipe.custom;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AnvilRecipe implements CustomRecipe {

    private NamespacedKey key;
    private ItemStack result;
    private ItemStack base;
    private ItemStack addition;
    private int costLevel = 0;
    private boolean copyNbt = true;

    public AnvilRecipe(NamespacedKey key, ItemStack result, ItemStack base, ItemStack addition) {
        this.key = key;
        this.result = result;
        this.base = base;
        this.addition = addition;
    }

    public AnvilRecipe setKey(NamespacedKey key) {
        this.key = key;
        return this;
    }

    public AnvilRecipe setResult(ItemStack result) {
        this.result = result;
        return this;
    }

    public ItemStack base() {
        return base;
    }

    public AnvilRecipe setBase(ItemStack base) {
        this.base = base;
        return this;
    }

    public ItemStack addition() {
        return addition;
    }

    public AnvilRecipe setAddition(ItemStack addition) {
        this.addition = addition;
        return this;
    }

    @Override
    public RecipeType recipeType() {
        return RecipeType.ANVIL;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public @NotNull ItemStack getResult() {
        return result;
    }

    public ItemStack result() {
        return result;
    }

    public int costLevel() {
        return costLevel;
    }

    public AnvilRecipe setCostLevel(int costLevel) {
        this.costLevel = costLevel;
        return this;
    }

    public boolean copyNbt() {
        return copyNbt;
    }

    public AnvilRecipe setCopyNbt(boolean copyNbt) {
        this.copyNbt = copyNbt;
        return this;
    }

}
