package pers.yufiria.craftorithm.ui.vanillaShaped;

import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

public class VanillaShapedIngredientIcon extends ItemDisplayIcon {

    private final Integer ingredientSlot;

    public VanillaShapedIngredientIcon(int ingredientSlot) {
        this.ingredientSlot = ingredientSlot;
    }

    public Integer ingredientSlot() {
        return ingredientSlot;
    }

    @Override
    public ItemStack display() {
        ItemStack display = super.display();
        display.setAmount(1);
        return display;
    }
}
