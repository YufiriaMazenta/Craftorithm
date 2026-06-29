package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.CrypticLibBukkit;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;

import java.util.Map;
import java.util.Optional;

public class BackIcon extends ActionIcon {
    public BackIcon(IconDisplay iconDisplay) {
        super(iconDisplay);
    }

    public BackIcon(IconDisplay iconDisplay, @NotNull Map<ClickType, CompiledScript> actions) {
        super(iconDisplay, actions);
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
        runActions(event, this.actions);
        return this;
    }
}
