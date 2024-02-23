package com.github.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRecipe<T> implements IRecipe<T> {

    protected Integer priority;
    protected NamespacedKey recipeKey;
    protected String result;

    public BaseRecipe(NamespacedKey recipeKey) {
        this.recipeKey = recipeKey;
    }

    @Override
    @NotNull
    public NamespacedKey key() {
        return recipeKey;
    }

    @Override
    public @Nullable Integer priority() {
        return this.priority;
    }

    @Override
    public void setPriority(@Nullable Integer priority) {
        this.priority = priority;
    }

    public BaseRecipe<T> setResult(String result) {
        this.result = result;
        return this;
    }

    @Override
    public String result() {
        return result;
    }
}
