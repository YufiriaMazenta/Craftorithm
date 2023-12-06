package com.github.yufiriamazenta.craftorithm.recipe.registry;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeRegistry {

    private NamespacedKey namespacedKey;
    private ItemStack result;
    private String group;

    public RecipeRegistry(@Nullable String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        this.group = group;
        this.namespacedKey = namespacedKey;
        this.result = result;
    }

    public NamespacedKey namespacedKey() {
        return namespacedKey;
    }

    public RecipeRegistry setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        return this;
    }

    public ItemStack result() {
        return result;
    }

    public RecipeRegistry setResult(ItemStack result) {
        this.result = result;
        return this;
    }

    @Nullable
    public String group() {
        return group;
    }

    public RecipeRegistry setGroup(@NotNull String group) {
        this.group = group;
        return this;
    }

    public abstract void register();

}
