package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.DefRecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StoneCuttingRecipeRegistry extends RecipeRegistry {

    private RecipeChoice source;

    public StoneCuttingRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(result(), "Recipe result cannot be null");
        Objects.requireNonNull(source, "Recipe ingredient cannot be null");
        StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(namespacedKey(), result(), source);
        stonecuttingRecipe.setGroup(group());
        DefRecipeManager.INSTANCE.regRecipe(group(), stonecuttingRecipe, RecipeType.STONE_CUTTING);
    }

    public RecipeChoice source() {
        return source;
    }

    public StoneCuttingRecipeRegistry setSource(RecipeChoice source) {
        this.source = source;
        return this;
    }
}
