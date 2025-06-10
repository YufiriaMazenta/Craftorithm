package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Multipage;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PreviousIcon extends LockableIcon {

    public PreviousIcon(@NotNull IconDisplay iconDisplay, @NotNull Map<ClickType, Action> actions, @Nullable IconDisplay lockedDisplay, @NotNull Map<ClickType, Action> lockedActions) {
        super(iconDisplay, actions, lockedDisplay, lockedActions);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (!locked) {
            Player player = (Player) event.getWhoClicked();
            MenuHelper.getOpeningMenu(player).ifPresent(
                menu -> {
                    if (menu instanceof Multipage) {
                        ((Multipage) menu).previousPage();
                    }
                }
            );

        }
        return super.onClick(event);
    }

}
