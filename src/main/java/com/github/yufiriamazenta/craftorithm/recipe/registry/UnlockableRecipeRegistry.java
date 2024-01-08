package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class UnlockableRecipeRegistry extends RecipeRegistry {

    protected boolean unlock = PluginConfigs.DEFAULT_RECIPE_UNLOCK.value();

    public UnlockableRecipeRegistry(@Nullable String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
    }

    public boolean unlock() {
        return unlock;
    }

    public UnlockableRecipeRegistry setUnlock(boolean unlock) {
        this.unlock = unlock;
        return this;
    }
    
}
