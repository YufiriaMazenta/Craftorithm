package com.github.yufiriamazenta.craftorithm.recipe.registry;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class RecipeRegistry {

    private NamespacedKey namespacedKey;
    private ItemStack result;

    public RecipeRegistry() {
    }

    public RecipeRegistry(NamespacedKey namespacedKey, ItemStack result) {
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

    protected abstract void register();

}
