package top.oasismc.oasisrecipe.cmd.subcmd;

import top.oasismc.oasisrecipe.api.cmd.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.cmd.subcmd.item.ItemGiveCommand;
import top.oasismc.oasisrecipe.cmd.subcmd.item.ItemSaveCommand;

public final class ItemCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super("item");
        regSubCommand(ItemSaveCommand.INSTANCE);
        regSubCommand(ItemGiveCommand.INSTANCE);
    }

}
