package pers.yufiria.craftorithm.ui.custom;

import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.script.ScriptContext;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.TranslatableMenu;

public class CustomMenu extends TranslatableMenu implements BackableMenu {

    private Menu parentMenu;
    private final CustomMenuInfo menuInfo;

    public CustomMenu(@NotNull Player player, @NotNull CustomMenuInfo menuInfo) {
        super(player, menuInfo.menuDisplay());
        this.menuInfo = menuInfo;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = player();
        String menuPerm = menuInfo.permission();
        if (menuPerm != null) {
            if (player != null && !player.hasPermission(menuPerm)) {
                event.setCancelled(true);
                return;
            }
        }
        CompiledScript openAction = menuInfo.openAction();
        if (openAction != null) {
            openAction.execute(new ScriptContext(player));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = player();
        CompiledScript closeAction = menuInfo.closeAction();
        if (closeAction != null) {
            closeAction.execute(new ScriptContext(player));
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
