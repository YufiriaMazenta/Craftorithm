package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum CustomModelDataComponent implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "custom_model_data_component";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        resultMeta.setCustomModelDataComponent(baseMeta.getCustomModelDataComponent());
        return resultMeta;
    }

}
