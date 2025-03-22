package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum ItemName implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "item_name";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasItemName()) {
            resultMeta.setItemName(baseMeta.getItemName());
        }
        return resultMeta;
    }

}
