package pers.yufiria.craftorithm.command.item.fuel;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;

public class FuelCommand extends CommandNode {

    public static final FuelCommand INSTANCE = new FuelCommand();

    protected FuelCommand() {
        super(CommandInfo.builder("fuel").permission(new PermInfo("craftorithm.command.fuel")).build());
    }

    @Subcommand
    CommandNode add = AddCommand.INSTANCE;

    @Subcommand
    CommandNode remove = RemoveCommand.INSTANCE;

}
