package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum DisplayName implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "display_name";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasDisplayName()) {
            resultMeta.setDisplayName(baseMeta.getDisplayName());
        }
        return resultMeta;
    }
}
