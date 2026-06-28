package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.menu.Multipage;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.Optional;

public class PrevPageIcon extends TranslatableIcon {

    public PrevPageIcon(IconDisplay iconDisplay) {
        super(iconDisplay);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        Optional<Menu> openingMenu = MenuHelper.getOpeningMenu((Player) event.getWhoClicked());
        openingMenu.ifPresent((menu -> {
            if (menu instanceof Multipage multipage) {
                multipage.previousPage();
            }
        }));
        return this;
    }
}
