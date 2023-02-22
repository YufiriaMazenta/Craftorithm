package me.yufiria.craftorithm.cmd.subcmd;

import me.yufiria.craftorithm.api.cmd.AbstractSubCommand;
import me.yufiria.craftorithm.cmd.subcmd.item.ItemSaveCommand;
import org.bukkit.command.CommandSender;
import me.yufiria.craftorithm.api.cmd.ISubCommand;
import me.yufiria.craftorithm.cmd.subcmd.item.ItemGiveCommand;

import java.util.List;

public final class ItemCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super("item");
        regSubCommand(ItemSaveCommand.INSTANCE);
        regSubCommand(ItemGiveCommand.INSTANCE);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        return super.onCommand(sender, args);
    }
}
