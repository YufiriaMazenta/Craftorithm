package pers.yufiria.craftorithm.command.item;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import pers.yufiria.craftorithm.command.item.fuel.FuelCommand;

public final class ItemCommand extends CommandNode {

    public static final ItemCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super(CommandInfo.builder("item").permission(new PermInfo("craftorithm.command.item")).build());
    }

    @Subcommand
    CommandNode save = SaveItemCommand.INSTANCE;

    @Subcommand
    CommandNode give = GiveItemCommand.INSTANCE;

    @Subcommand
    CommandNode fuel = FuelCommand.INSTANCE;

}
