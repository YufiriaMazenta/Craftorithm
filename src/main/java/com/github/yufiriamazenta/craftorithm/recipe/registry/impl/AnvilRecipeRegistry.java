package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.google.common.base.Preconditions;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AnvilRecipeRegistry extends RecipeRegistry {

    private ItemStack base, addition;
    private boolean copyNbt = false;
    private int costLevel;
    private boolean copyEnchantments = true;

    public AnvilRecipeRegistry(@Nullable String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
        this.copyNbt = true;
        this.costLevel = 0;
    }

    @Override
    public void register() {
        Preconditions.checkArgument(!ItemHelper.isAir(base), "Recipe base cannot be null");
        Preconditions.checkArgument(!ItemHelper.isAir(addition), "Recipe base cannot be null");
        Preconditions.checkArgument(!ItemHelper.isAir(result()), "Recipe base cannot be null");
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        AnvilRecipe anvilRecipe = new AnvilRecipe(namespacedKey(), result(), base, addition);
        anvilRecipe.setCopyNbt(copyNbt);
        anvilRecipe.setCostLevel(costLevel);
        anvilRecipe.setCopyEnchantments(copyEnchantments);
        RecipeManager.INSTANCE.regRecipe(group(), anvilRecipe, RecipeType.ANVIL);
    }

    public ItemStack base() {
        return base;
    }

    public AnvilRecipeRegistry setBase(ItemStack base) {
        this.base = base;
        return this;
    }

    public ItemStack addition() {
        return addition;
    }

    public AnvilRecipeRegistry setAddition(ItemStack addition) {
        this.addition = addition;
        return this;
    }

    public boolean copyNbt() {
        return copyNbt;
    }

    public AnvilRecipeRegistry setCopyNbt(boolean copyNbt) {
        this.copyNbt = copyNbt;
        return this;
    }

    public int costLevel() {
        return costLevel;
    }

    public AnvilRecipeRegistry setCostLevel(int costLevel) {
        this.costLevel = costLevel;
        return this;
    }

    public boolean copyEnchantments() {
        return copyEnchantments;
    }

    public AnvilRecipeRegistry setCopyEnchantments(boolean copyEnchantments) {
        this.copyEnchantments = copyEnchantments;
        return this;
    }

}
