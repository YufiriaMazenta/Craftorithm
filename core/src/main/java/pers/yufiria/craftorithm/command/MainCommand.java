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
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.permission.Permissions;

@Command(platforms = {PlatformSide.BUKKIT})
public class MainCommand extends CommandTree {

    public static final MainCommand INSTANCE = new MainCommand();

    MainCommand() {
        super(
            CommandInfo
                .builder("craftorithm")
                .permission(new PermInfo(Permissions.COMMAND))
                .aliases(PluginConfigs.MAIN_COMMAND_ALIASES.value())
                .build()
        );
    }

    @Subcommand
    CommandNode reload = ReloadCommand.INSTANCE;

    @Subcommand
    CommandNode version = VersionCommand.INSTANCE;

    @Subcommand
    CommandNode remove = RemoveCommand.INSTANCE;

    @Subcommand
    CommandNode disable = DisableCommand.INSTANCE;

    @Subcommand
    CommandNode restore = RestoreCommand.INSTANCE;

    @Subcommand
    CommandNode item = ItemCommand.INSTANCE;

    @Subcommand
    CommandNode create = CreateCommand.INSTANCE;

    @Subcommand
    CommandNode display = DisplayCommand.INSTANCE;

    @Subcommand
    CommandNode openMenu = OpenMenuCommand.INSTANCE;

    @Subcommand
    CommandNode script = ScriptCommand.INSTANCE;

    @Subcommand
    CommandNode edit = EditCommand.INSTANCE;

    @Subcommand
    CommandNode recipebook = RecipeBookCommand.INSTANCE;

    @Subcommand
    CommandNode discover = DiscoverCommand.INSTANCE;

    @Subcommand
    CommandNode undiscover = UndiscoverCommand.INSTANCE;

}
