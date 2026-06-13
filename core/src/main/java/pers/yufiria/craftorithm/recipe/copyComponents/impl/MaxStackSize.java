package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum MaxStackSize implements CopyComponentsRule {

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
