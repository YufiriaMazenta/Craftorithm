package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

public class VanillaShapelessIngredientIcon extends ItemDisplayIcon {

    private final Integer ingredientSlot;

    public VanillaShapelessIngredientIcon(int ingredientSlot) {
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
