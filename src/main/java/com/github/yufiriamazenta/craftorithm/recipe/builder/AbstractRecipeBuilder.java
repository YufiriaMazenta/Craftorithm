package com.github.yufiriamazenta.craftorithm.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractRecipeBuilder {

    private ItemStack result;
    private NamespacedKey key;

    protected AbstractRecipeBuilder() {}

    public ItemStack getResult() {
        return result;
    }

    public AbstractRecipeBuilder result(ItemStack result) {
        this.result = result;
        return this;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public AbstractRecipeBuilder key(NamespacedKey key) {
        this.key = key;
        return this;
    }

}
