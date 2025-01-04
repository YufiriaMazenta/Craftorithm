package pers.yufiria.craftorithm.recipe.nbt.impl;

import com.google.errorprone.annotations.Keep;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.nbt.KeepNbtRule;

public enum Lore implements KeepNbtRule {

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
