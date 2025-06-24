package pers.yufiria.craftorithm.ui.creator.vanillaShaped;

import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.creator.VanillaShapedCreator;

public class VanillaShapedCreatorMenu extends Menu {

    public VanillaShapedCreatorMenu(@NotNull Player player) {
        super(player, () -> {
            MenuDisplay menuDisplay = new MenuDisplay(
                VanillaShapedCreator.TITLE.value(),
                new MenuLayout()//TODO
            );
            return menuDisplay;
        });
    }



}
