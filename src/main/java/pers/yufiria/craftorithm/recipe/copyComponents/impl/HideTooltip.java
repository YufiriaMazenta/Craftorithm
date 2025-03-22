package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum HideTooltip implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "hide_tooltip";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        resultMeta.setHideTooltip(baseMeta.isHideTooltip());
        return resultMeta;
    }
}
