package pers.yufiria.craftorithm.cmd.sub.item;

import pers.yufiria.craftorithm.cmd.sub.item.fuel.FuelCommand;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;

public final class ItemCommand extends BukkitSubcommand {

    public static final ItemCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super(CommandInfo.builder("item").permission(new PermInfo("craftorithm.command.item")).build());
        regSub(SaveItemCommand.INSTANCE).regSub(GiveItemCommand.INSTANCE).regSub(FuelCommand.INSTANCE);
    }

    @Subcommand
    BukkitSubcommand save = SaveItemCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand give = GiveItemCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand fuel = FuelCommand.INSTANCE;

}
