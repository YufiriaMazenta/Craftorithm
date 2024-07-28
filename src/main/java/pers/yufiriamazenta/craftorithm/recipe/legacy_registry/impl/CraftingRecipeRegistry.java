package pers.yufiriamazenta.craftorithm.recipe.legacy_registry.impl;

import pers.yufiriamazenta.craftorithm.recipe.legacy_registry.RecipeRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CraftingRecipeRegistry extends RecipeRegistry {

    protected @Nullable CraftingBookCategory craftingBookCategory;

    public CraftingRecipeRegistry(@Nullable String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
    }

    public @Nullable CraftingBookCategory getCraftingBookCategory() {
        return craftingBookCategory;
    }

    public CraftingRecipeRegistry setCraftingBookCategory(CraftingBookCategory craftingBookCategory) {
        this.craftingBookCategory = craftingBookCategory;
        return this;
    }

}

