package pers.yufiria.craftorithm.ui.display;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class RecipeResultIcon extends ItemDisplayIcon {

    public RecipeResultIcon() {
        super();
    }

    public RecipeResultIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
