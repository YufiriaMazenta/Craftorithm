package pers.yufiria.craftorithm.recipe.nbt.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.nbt.KeepNbtRule;

public enum Food implements KeepNbtRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "food";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasFood()) {
            resultMeta.setFood(baseMeta.getFood());
        }
        return resultMeta;
    }
}
