package pers.yufiria.craftorithm.recipe.keepNbt.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.keepNbt.CopyNbtRule;

public enum Lore implements CopyNbtRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "lore";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasLore()) {
            resultMeta.setLore(baseMeta.getLore());
        }
        return resultMeta;
    }
}
