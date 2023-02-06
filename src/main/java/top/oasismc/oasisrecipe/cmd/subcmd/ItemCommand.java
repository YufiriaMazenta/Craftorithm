package top.oasismc.oasisrecipe.cmd.subcmd;

import top.oasismc.oasisrecipe.api.cmd.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;

import java.util.HashMap;

public final class ItemCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super("item", new HashMap<>());
        regSubCommand(ItemSaveCommand.INSTANCE);
        regSubCommand(ItemGetCommand.INSTANCE);
    }

}
