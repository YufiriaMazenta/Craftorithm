package pers.yufiria.craftorithm.recipe.keepNbt.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtRule;

public enum ItemFlag implements KeepNbtRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "item_flag";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        resultMeta.removeItemFlags(org.bukkit.inventory.ItemFlag.values());
        baseMeta.getItemFlags().forEach(resultMeta::addItemFlags);
        return resultMeta;
    }
}
