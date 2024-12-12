package pers.yufiria.craftorithm.ui.action;

import crypticlib.action.BaseAction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Close extends BaseAction {

    public Close(String ignored) {}

    @Override
    public String toActionStr() {
        return "close";
    }

    @Override
    public void run(Player player, Plugin plugin) {
        player.closeInventory();
        runNext(player, plugin);
    }
}
