package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.item.ItemManager;

import java.util.List;

public final class DebugCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new DebugCommand();

    private DebugCommand() {
        super("debug", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp())
                return true;
            for (String s : ItemManager.getItemMap().keySet()) {
                player.getInventory().addItem(ItemManager.getItemMap().get(s));
            }
        }
        return true;
    }

}
