package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;

public class FuelCommand extends BukkitSubcommand {

    public static final FuelCommand INSTANCE = new FuelCommand();

    protected FuelCommand() {
        super(CommandInfo.builder("fuel").permission(new PermInfo("craftorithm.command.fuel")).build());
    }

    @Subcommand
    BukkitSubcommand add = AddFuelCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand remove = RemoveFuelCommand.INSTANCE;

}
