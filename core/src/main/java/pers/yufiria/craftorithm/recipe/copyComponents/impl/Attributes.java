package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum Attributes implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "attributes";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasAttributeModifiers()) {
            resultMeta.setAttributeModifiers(baseMeta.getAttributeModifiers());
        }
        return resultMeta;
    }
}
