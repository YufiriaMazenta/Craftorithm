package pers.yufiria.craftorithm.recipe.nbt.impl;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.nbt.KeepNbtRule;

public enum Enchantments implements KeepNbtRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return "enchantments";
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        if (baseMeta.hasEnchants()) {
            baseMeta.getEnchants().forEach((enchant, level) -> {
                if (resultMeta.hasEnchant(enchant)) {
                    int resultEnchantLevel = resultMeta.getEnchantLevel(enchant);
                    if (resultEnchantLevel > level) {
                        return;
                    }
                    resultMeta.removeEnchant(enchant);
                    resultMeta.addEnchant(enchant, resultEnchantLevel, true);
                } else {
                    resultMeta.addEnchant(enchant, level, true);
                }
            });
        }
        return resultMeta;
    }
}
