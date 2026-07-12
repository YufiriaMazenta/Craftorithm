package pers.yufiria.craftorithm.command.item;

import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.command.item.fuel.FuelCommand;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;

public final class ItemCommand extends CommandNode {

    public static final ItemCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super(CommandInfo.builder("item").permission(new PermInfo("craftorithm.command.item")).build());
    }

    @Override
    public void onNoPerm(@NotNull Invoker invoker, @NotNull List<String> args) {
        LangUtils.sendLang(invoker, Languages.COMMAND_NO_PERM);
    }

    @Subcommand
    CommandNode save = SaveItemCommand.INSTANCE;

    @Subcommand
    CommandNode give = GiveItemCommand.INSTANCE;

    @Subcommand
    CommandNode fuel = FuelCommand.INSTANCE;

}
