package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel.FuelCommand;
import crypticlib.command.SubcommandHandler;
import crypticlib.command.annotation.Subcommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ItemCommand extends AbstractSubCommand {

    public static final ItemCommand INSTANCE = new ItemCommand();

    @Subcommand
    private SubcommandHandler save = SaveItemCommand.INSTANCE;
    @Subcommand
    private SubcommandHandler give = GiveItemCommand.INSTANCE;
    @Subcommand
    private SubcommandHandler fuel = FuelCommand.INSTANCE;

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
