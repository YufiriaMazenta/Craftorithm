package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel.FuelCommand;
import crypticlib.command.CommandTreeNode;
import crypticlib.command.annotation.CommandNode;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ItemCommand extends AbstractSubCommand {

    public static final ItemCommand INSTANCE = new ItemCommand();

    @CommandNode
    private CommandTreeNode save = SaveItemCommand.INSTANCE;
    @CommandNode
    private CommandTreeNode give = GiveItemCommand.INSTANCE;
    @CommandNode
    private CommandTreeNode fuel = FuelCommand.INSTANCE;

    private ItemCommand() {
        super("item", "craftorithm.command.item");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 2);
            return true;
        }
        return super.onCommand(sender, args);
    }

}
