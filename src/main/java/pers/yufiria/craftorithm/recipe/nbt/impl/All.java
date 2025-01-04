package pers.yufiria.craftorithm.recipe.nbt.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.nbt.KeepNbtRule;

public enum All implements KeepNbtRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "all";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        return baseMeta;
    }

}
