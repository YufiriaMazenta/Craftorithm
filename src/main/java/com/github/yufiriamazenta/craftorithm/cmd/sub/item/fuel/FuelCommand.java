package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import crypticlib.command.CommandTreeNode;
import crypticlib.command.annotation.CommandNode;

public class FuelCommand extends AbstractSubCommand {

    public static final FuelCommand INSTANCE = new FuelCommand();
    @CommandNode
    private CommandTreeNode add = AddFuelCommand.INSTANCE;
    @CommandNode
    private CommandTreeNode remove = RemoveFuelCommand.INSTANCE;

    protected FuelCommand() {
        super("fuel", "craftorithm.command.item.fuel");
    }

}
