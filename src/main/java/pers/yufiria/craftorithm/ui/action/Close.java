package pers.yufiria.craftorithm.ui.action;

import crypticlib.action.BaseAction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Close extends BaseAction {

    public Close(String ignored) {}

    @Override
    public String toActionStr() {
        return "close";
    }

    @Override
    public void run(@Nullable Player player, @NotNull Plugin plugin, @Nullable Function<String, String> function) {
        if (player != null) {
            player.closeInventory();
        }
        runNext(player, plugin, function);
    }

}
