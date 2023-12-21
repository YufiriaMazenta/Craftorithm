package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import crypticlib.command.ISubcmdExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ItemCommand extends AbstractSubCommand {

    public static final ISubcmdExecutor INSTANCE = new ItemCommand();

    private ItemCommand() {
        super("item", "craftorithm.command.item");
        regSub(SaveCommand.INSTANCE).regSub(GiveCommand.INSTANCE);
    }

    @Override
    public Boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        return super.onCommand(sender, args);
    }
}
