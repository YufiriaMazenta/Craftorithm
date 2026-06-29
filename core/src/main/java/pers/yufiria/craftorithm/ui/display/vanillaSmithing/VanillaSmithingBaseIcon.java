package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaSmithingBaseIcon extends ItemDisplayIcon {

    public VanillaSmithingBaseIcon() {
        super();
    }

    public VanillaSmithingBaseIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
