package com.github.yufiriamazenta.craftorithm.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public abstract class AbstractRecipeBuilder {

    private ItemStack result;
    private NamespacedKey key;

    public ItemStack result() {
        return result.clone();
    }

    public AbstractRecipeBuilder setResult(ItemStack result) {
        this.result = result;
        return this;
    }

    public NamespacedKey key() {
        return key;
    }

    public AbstractRecipeBuilder setKey(NamespacedKey key) {
        this.key = key;
        return this;
    }

    public abstract Recipe build();

}
