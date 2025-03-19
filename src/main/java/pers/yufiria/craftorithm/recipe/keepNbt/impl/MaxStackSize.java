package pers.yufiria.craftorithm.recipe.keepNbt.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtRule;

public enum MaxStackSize implements KeepNbtRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "max_stack_size";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasMaxStackSize()) {
            resultMeta.setMaxStackSize(baseMeta.getMaxStackSize());
        }
        return resultMeta;
    }
}
