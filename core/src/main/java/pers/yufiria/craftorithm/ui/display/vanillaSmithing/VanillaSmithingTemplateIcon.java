package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.script.compile.CompiledScript;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaSmithingTemplateIcon extends ItemDisplayIcon {

    public VanillaSmithingTemplateIcon() {
        super();
    }

    public VanillaSmithingTemplateIcon(@NotNull Map<ClickType, CompiledScript> actions) {
        super(actions);
    }
}
