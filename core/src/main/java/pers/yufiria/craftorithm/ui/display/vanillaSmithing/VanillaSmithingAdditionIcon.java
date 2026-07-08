package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.script.compile.CompiledScript;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaSmithingAdditionIcon extends ItemDisplayIcon {

    public VanillaSmithingAdditionIcon() {
        super();
    }

    public VanillaSmithingAdditionIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
