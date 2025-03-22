package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum ItemModel implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "item_model";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasItemModel()) {
            resultMeta.setItemModel(baseMeta.getItemModel());
        }
        return resultMeta;
    }

}
