package pers.yufiria.craftorithm.ui.display.vanillaSmelting;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaSmeltingIngredientIcon extends ItemDisplayIcon {

    public VanillaSmeltingIngredientIcon() {
        super();
    }

    public VanillaSmeltingIngredientIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
