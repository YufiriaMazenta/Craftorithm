package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
public class FuelCommand extends AbstractSubCommand {

    public static final FuelCommand INSTANCE = new FuelCommand();

    protected FuelCommand() {
        super("fuel", "craftorithm.command.item.fuel");
        regSub(AddFuelCommand.INSTANCE).regSub(RemoveFuelCommand.INSTANCE);
    }

}
