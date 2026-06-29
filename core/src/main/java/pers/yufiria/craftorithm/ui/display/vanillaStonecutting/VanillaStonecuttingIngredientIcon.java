package pers.yufiria.craftorithm.ui.display.vanillaStonecutting;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaStonecuttingIngredientIcon extends ItemDisplayIcon {

    public VanillaStonecuttingIngredientIcon() {
        super();
    }

    public VanillaStonecuttingIngredientIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
