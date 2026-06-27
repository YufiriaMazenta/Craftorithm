package pers.yufiria.craftorithm.ui.icon;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pers.yufiria.craftorithm.ui.BackableMenu;

import java.util.Optional;

public class BackIcon extends TranslatableIcon {
    public BackIcon(IconDisplay iconDisplay) {
        super(iconDisplay);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        Optional<Menu> openingMenu = MenuHelper.getOpeningMenu((Player) event.getWhoClicked());
        openingMenu.ifPresent((menu -> {
            if (menu instanceof BackableMenu backableMenu) {
                Menu parentMenu = backableMenu.parentMenu();
                if (parentMenu != null) {
                    parentMenu.openMenu();
                } else {
                    CrypticLibBukkit.scheduler().async(() -> event.getWhoClicked().closeInventory());
                }
            } else {
                CrypticLibBukkit.scheduler().async(() -> event.getWhoClicked().closeInventory());
            }
        }));
        return this;
    }
}
