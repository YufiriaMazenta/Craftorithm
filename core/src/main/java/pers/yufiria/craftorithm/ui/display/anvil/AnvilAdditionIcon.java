package pers.yufiria.craftorithm.ui.display.anvil;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class AnvilAdditionIcon extends ItemDisplayIcon {

    public AnvilAdditionIcon() {
        super();
    }

    public AnvilAdditionIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
