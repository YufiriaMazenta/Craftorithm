package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum FireResistance implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "fire_resistance";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        resultMeta.setFireResistant(baseMeta.isFireResistant());
        return resultMeta;
    }

}
