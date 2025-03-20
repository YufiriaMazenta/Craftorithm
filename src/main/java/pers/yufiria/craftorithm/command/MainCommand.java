package pers.yufiria.craftorithm.command;

import pers.yufiria.craftorithm.command.item.ItemCommand;
import pers.yufiria.craftorithm.command.recipe.*;
import crypticlib.command.BukkitCommand;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;

import java.util.Arrays;

@Command
public class MainCommand extends BukkitCommand {

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
    BukkitSubcommand reload = ReloadCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand version = VersionCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand remove = RemoveRecipeCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand disable = DisableRecipeCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand item = ItemCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand create = CreateRecipeCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand list = RecipeListCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand display = DisplayRecipeCommand.INSTANCE;

}
