package com.github.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRecipe<T> implements IRecipe<T> {

    protected Integer priority;
    protected NamespacedKey recipeKey;

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
}
