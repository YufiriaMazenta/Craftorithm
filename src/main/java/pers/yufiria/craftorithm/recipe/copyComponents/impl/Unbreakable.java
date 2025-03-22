package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum Unbreakable implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "unbreakable";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        resultMeta.setUnbreakable(baseMeta.isUnbreakable());
        return resultMeta;
    }

}
