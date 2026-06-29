package pers.yufiria.craftorithm.ui.display.vanillaShaped;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaShapedIngredientIcon extends ItemDisplayIcon {

    private final Integer ingredientSlot;

    public VanillaShapedIngredientIcon(int ingredientSlot) {
        this.ingredientSlot = ingredientSlot;
    }

    public VanillaShapedIngredientIcon(int ingredientSlot, @NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
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
