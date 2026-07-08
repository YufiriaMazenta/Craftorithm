package pers.yufiria.craftorithm.ui.display.vanillaBrewing;

import crypticlib.script.compile.CompiledScript;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaBrewingIngredientIcon extends ItemDisplayIcon {

    public VanillaBrewingIngredientIcon() {
        super();
    }

    public VanillaBrewingIngredientIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
