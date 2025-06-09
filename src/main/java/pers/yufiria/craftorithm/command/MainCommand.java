package pers.yufiria.craftorithm.command;

import crypticlib.PlatformSide;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.command.CommandTree;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import pers.yufiria.craftorithm.command.item.ItemCommand;
import pers.yufiria.craftorithm.command.menu.OpenMenuCommand;
import pers.yufiria.craftorithm.command.recipe.*;

import java.util.Arrays;

@Command(platforms = {PlatformSide.BUKKIT})
public class MainCommand extends CommandTree {

    public static final MainCommand INSTANCE = new MainCommand();

    MainCommand() {
        super(
            CommandInfo
                .builder("craftorithm")
                .permission(new PermInfo("craftorithm.command"))
                .aliases(Arrays.asList("craft", "cra", "crafto"))
                .build()
        );
    }

    @Subcommand
    CommandNode reload = ReloadCommand.INSTANCE;

    @Subcommand
    CommandNode version = VersionCommand.INSTANCE;

    @Subcommand
    CommandNode remove = RemoveRecipeCommand.INSTANCE;

    @Subcommand
    CommandNode disable = DisableRecipeCommand.INSTANCE;

    @Subcommand
    CommandNode item = ItemCommand.INSTANCE;

    @Subcommand
    CommandNode create = CreateRecipeCommand.INSTANCE;

    @Subcommand
    CommandNode display = DisplayRecipeCommand.INSTANCE;

    @Subcommand
    CommandNode openMenu = OpenMenuCommand.INSTANCE;

}
