package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel.FuelCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ItemCommand extends AbstractSubCommand {

    public static final ItemCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super("item", "craftorithm.command.item");
        regSub(SaveItemCommand.INSTANCE).regSub(GiveItemCommand.INSTANCE).regSub(FuelCommand.INSTANCE);
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
