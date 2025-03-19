package pers.yufiria.craftorithm.recipe.keepNbt.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtRule;

public enum DisplayName implements KeepNbtRule {

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
