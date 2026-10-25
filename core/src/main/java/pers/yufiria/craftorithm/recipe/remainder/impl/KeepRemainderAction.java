package pers.yufiria.craftorithm.recipe.remainder.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.remainder.RemainderAction;

/**
 * 原样保留被消耗的物品
 */
public enum KeepRemainderAction implements RemainderAction {

    INSTANCE;

    public static final String KEY = "keep";

    @Override
    public @NotNull ItemStack createRemainder(@NotNull ItemStack source) {
        ItemStack remainder = source.clone();
        remainder.setAmount(1);
        return remainder;
    }

}
