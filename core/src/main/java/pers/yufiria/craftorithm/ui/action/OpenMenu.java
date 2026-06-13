package pers.yufiria.craftorithm.ui.action;

import crypticlib.action.BaseAction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.ui.custom.CustomMenuManager;

import java.util.Objects;
import java.util.function.Function;

public class OpenMenu extends BaseAction {

    private String menuName;

    public OpenMenu(String menuName) {
        this.menuName = Objects.requireNonNull(menuName);
    }

    @Override
    public String toActionStr() {
        return "openmenu " + menuName;
    }

    @Override
    public void run(@Nullable Player player, @NotNull Plugin plugin, @Nullable Function<String, String> function) {
        CustomMenuManager.INSTANCE.openMenu(player, menuName, result -> {});
        runNext(player, plugin, function);
    }
}
