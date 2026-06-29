package pers.yufiria.craftorithm.ui.display.vanillaBrewing;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaBrewingInputIcon extends ItemDisplayIcon {

    public VanillaBrewingInputIcon() {
        super();
    }

    public VanillaBrewingInputIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
