package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 1.20以下的锻造台配方注册器
 */
public class SmithingRecipeRegistry extends RecipeRegistry {

    private RecipeChoice base, addition;
    protected boolean copyNbt = true;

    public SmithingRecipeRegistry(@NotNull String recipeGroup, @NotNull NamespacedKey namespacedKey, ItemStack result) {
        super(recipeGroup, namespacedKey, result);
    }

    public RecipeChoice base() {
        return base;
    }

    public SmithingRecipeRegistry setBase(RecipeChoice base) {
        this.base = base;
        return this;
    }

    public RecipeChoice addition() {
        return addition;
    }

    public SmithingRecipeRegistry setAddition(RecipeChoice addition) {
        this.addition = addition;
        return this;
    }

    public boolean copyNbt() {
        return copyNbt;
    }

    public SmithingRecipeRegistry setCopyNbt(boolean copyNbt) {
        this.copyNbt = copyNbt;
        return this;
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(result(), "Recipe result cannot be null");
        Objects.requireNonNull(base, "Recipe base cannot be null");
        Objects.requireNonNull(addition, "Recipe addition cannot be null");

        SmithingRecipe smithingRecipe;
        if (CrypticLib.isPaper())
            smithingRecipe = new SmithingRecipe(namespacedKey(), result(), base, addition, copyNbt);
        else
            smithingRecipe = new SmithingRecipe(namespacedKey(), result(), base, addition);
        RecipeManager.INSTANCE.regRecipe(group(), smithingRecipe, RecipeType.SMITHING);
    }
}
