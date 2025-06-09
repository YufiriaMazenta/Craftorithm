package pers.yufiria.craftorithm.ui.action;

import crypticlib.action.BaseAction;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import crypticlib.util.IOHelper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.ui.BackableMenu;

import java.util.Optional;
import java.util.function.Function;

public class Back extends BaseAction {

    public Back(String ignored) {}

    @Override
    public String toActionStr() {
        return "back";
    }

    @Override
    public void run(@Nullable Player player, @NotNull Plugin plugin, @Nullable Function<String, String> function) {
        if (player != null) {
            Optional<Menu> openingMenuOpt = MenuHelper.getOpeningMenu(player);
            IOHelper.info("is opening menu: " + openingMenuOpt.isPresent());
            if (openingMenuOpt.isPresent()) {
                Menu menu = openingMenuOpt.get();
                if (menu instanceof BackableMenu backableMenu) {
                    Menu parentMenu = backableMenu.parentMenu();
                    if (parentMenu != null) {
                        parentMenu.openMenu();
                    } else {
                        player.closeInventory();
                    }
                } else {
                    player.closeInventory();
                }
            }
        }
        runNext(player, plugin, function);
    }

}
