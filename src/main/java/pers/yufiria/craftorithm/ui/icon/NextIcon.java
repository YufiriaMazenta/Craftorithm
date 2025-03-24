package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Multipage;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NextIcon extends LockableIcon {

    public NextIcon(@NotNull IconDisplay iconDisplay, @Nullable Action action, @Nullable IconDisplay lockedDisplay, @Nullable Action lockedAction) {
        super(iconDisplay, action, lockedDisplay, lockedAction);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (!locked) {
            Player player = (Player) event.getWhoClicked();
            MenuHelper.getOpeningMenu(player).ifPresent(
                menu -> {
                    if (menu instanceof Multipage) {
                        ((Multipage) menu).nextPage();
                    }
                }
            );
        }
        return super.onClick(event);
    }

}
