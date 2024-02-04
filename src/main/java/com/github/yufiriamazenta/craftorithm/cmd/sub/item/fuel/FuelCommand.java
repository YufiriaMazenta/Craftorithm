package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import crypticlib.command.SubcommandHandler;
import crypticlib.command.annotation.Subcommand;

public class FuelCommand extends AbstractSubCommand {

    public static final FuelCommand INSTANCE = new FuelCommand();
    @Subcommand
    private SubcommandHandler add = AddFuelCommand.INSTANCE;
    @Subcommand
    private SubcommandHandler remove = RemoveFuelCommand.INSTANCE;

    protected FuelCommand() {
        super("fuel", "craftorithm.command.item.fuel");
    }

}
