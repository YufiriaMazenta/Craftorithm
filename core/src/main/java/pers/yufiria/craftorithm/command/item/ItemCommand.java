package pers.yufiria.craftorithm.command.item;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import pers.yufiria.craftorithm.command.AbstractSubcommand;
import pers.yufiria.craftorithm.command.item.fuel.FuelCommand;
import pers.yufiria.craftorithm.permission.Permissions;

public final class ItemCommand extends AbstractSubcommand {

    public static final ItemCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super(CommandInfo.builder("item").permission(new PermInfo(Permissions.ITEM)).build());
    }


    @Subcommand
    CommandNode save = SaveCommand.INSTANCE;

    @Subcommand
    CommandNode give = GiveCommand.INSTANCE;

    @Subcommand
    CommandNode fuel = FuelCommand.INSTANCE;

    @Subcommand
    CommandNode itemGroupItem = GetItemGroupCommand.INSTANCE;

}
