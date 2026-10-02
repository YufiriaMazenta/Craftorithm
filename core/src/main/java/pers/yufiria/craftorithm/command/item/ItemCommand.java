package pers.yufiria.craftorithm.command.item;

import crypticlib.command.CommandContext;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.command.item.fuel.FuelCommand;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

public final class ItemCommand extends CommandNode {

    public static final ItemCommand INSTANCE = new ItemCommand();

    private ItemCommand() {
        super(CommandInfo.builder("item").permission(new PermInfo("craftorithm.command.item")).build());
    }

    @Override
    public void onNoPerm(@NotNull CommandContext context) {
        LangUtils.sendLang(context.invoker(), Languages.COMMAND_NO_PERM);
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
