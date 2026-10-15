package pers.yufiria.craftorithm.command.item.fuel;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;

public final class FuelCommand extends CommandNode {

    public static final FuelCommand INSTANCE = new FuelCommand();

    private FuelCommand() {
        super(CommandInfo.builder("fuel").build());
    }

    @Subcommand
    CommandNode add = AddCommand.INSTANCE;

    @Subcommand
    CommandNode remove = RemoveCommand.INSTANCE;

}
