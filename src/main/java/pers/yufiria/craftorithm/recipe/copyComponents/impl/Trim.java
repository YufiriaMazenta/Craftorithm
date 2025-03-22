package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

public enum Trim implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "trim";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (!(baseMeta instanceof ArmorMeta baseArmorMeta)) {
            return resultMeta;
        }
        if (!(resultMeta instanceof ArmorMeta resultArmorMeta)) {
            return resultMeta;
        }
        if (baseArmorMeta.hasTrim()) {
            resultArmorMeta.setTrim(baseArmorMeta.getTrim());
        }
        return resultArmorMeta;
    }

}
