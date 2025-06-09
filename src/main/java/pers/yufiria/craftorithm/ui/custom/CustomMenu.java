package pers.yufiria.craftorithm.ui.custom;

import crypticlib.action.Action;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.ui.BackableMenu;

public class CustomMenu extends Menu implements BackableMenu {

    private Menu parentMenu;
    private final CustomMenuInfo menuInfo;

    public CustomMenu(@NotNull Player player, @NotNull CustomMenuInfo menuInfo) {
        super(player, menuInfo.menuDisplay());
        this.menuInfo = menuInfo;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = player();
        if (menuInfo.permission() != null) {
            if (player != null && !player.hasPermission(menuInfo.permission())) {
                event.setCancelled(true);
                return;
            }
        }
        Action openAction = menuInfo.openAction();
        if (openAction != null) {
            openAction.run(player, Craftorithm.instance(), null);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = player();
        Action closeAction = menuInfo.closeAction();
        if (closeAction != null) {
            closeAction.run(player, Craftorithm.instance(), null);
        }
    }

    @Override
    public @Nullable Menu parentMenu() {
        return parentMenu;
    }

    @Override
    public void setParentMenu(@Nullable Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

}
