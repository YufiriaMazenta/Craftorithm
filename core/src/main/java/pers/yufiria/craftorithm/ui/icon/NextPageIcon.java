package pers.yufiria.craftorithm.ui.icon;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.menu.Multipage;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class NextPageIcon extends TranslatableIcon {
    public NextPageIcon(IconDisplay iconDisplay) {
        super(iconDisplay);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        Optional<Menu> openingMenu = MenuHelper.getOpeningMenu((Player) event.getWhoClicked());
        openingMenu.ifPresent((menu -> {
            if (menu instanceof Multipage multipage) {
                multipage.nextPage();
            }
        }));
        return this;
    }
}
