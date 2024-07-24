package com.github.yufiriamazenta.craftorithm.menu.display.icon;

import com.github.yufiriamazenta.craftorithm.menu.icon.ActionIcon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class IngredientIcon extends ActionIcon {

    protected ItemStack ingredient;

    public IngredientIcon() {
        super(new IconDisplay(Material.AIR));
    }

    @Override
    public ItemStack display() {
        if (ItemHelper.isAir(ingredient))
            return new ItemStack(Material.AIR);
        else
            return ingredient.clone();
    }

    public ItemStack ingredient() {
        return ingredient;
    }

    public IngredientIcon setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;
        return this;
    }

}
