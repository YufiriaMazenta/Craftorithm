package pers.yufiria.craftorithm.ui.custom;

import crypticlib.script.ScriptContext;
import crypticlib.script.compile.CompiledScript;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
        Player player = player().orElse(null);
        String menuPerm = menuInfo.permission();
        if (menuPerm != null) {
            if (player != null && !player.hasPermission(menuPerm)) {
                event.setCancelled(true);
                return;
            }
        }
        CompiledScript openAction = menuInfo.openAction();
        if (openAction != null) {
            if (player != null) {
                openAction.execute(new ScriptContext(player.getUniqueId()));
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = player().orElse(null);
        CompiledScript closeAction = menuInfo.closeAction();
        if (closeAction != null) {
            if (player != null) {
                closeAction.execute(new ScriptContext(player.getUniqueId()));
            }
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
