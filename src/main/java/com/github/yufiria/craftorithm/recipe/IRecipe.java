package com.github.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IRecipe<T> {

    @NotNull NamespacedKey key();

    ItemStack result();

    boolean match(T matchObj);

    @Nullable Integer priority();

    void setPriority(@Nullable Integer priority);

}
