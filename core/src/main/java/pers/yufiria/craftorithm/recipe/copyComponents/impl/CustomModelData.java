package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum CustomModelData implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "custom_model_data";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasCustomModelData()) {
            resultMeta.setCustomModelData(baseMeta.getCustomModelData());
        }
        return resultMeta;
    }
}
