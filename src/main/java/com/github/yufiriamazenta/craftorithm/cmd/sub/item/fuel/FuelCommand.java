package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import crypticlib.command.annotation.Subcommand;

public class FuelCommand extends AbstractSubCommand {

    public static final FuelCommand INSTANCE = new FuelCommand();

    protected FuelCommand() {
        super("fuel", "craftorithm.command.item.fuel");
    }

    @Subcommand AddFuelCommand add = AddFuelCommand.INSTANCE;

    @Subcommand RemoveFuelCommand removeFuelCommand = RemoveFuelCommand.INSTANCE;

}
