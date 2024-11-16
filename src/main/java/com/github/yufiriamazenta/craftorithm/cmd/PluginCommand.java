package com.github.yufiriamazenta.craftorithm.cmd;

import com.github.yufiriamazenta.craftorithm.cmd.sub.ReloadCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.RunArcencielCmd;
import com.github.yufiriamazenta.craftorithm.cmd.sub.VersionCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.item.ItemCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.recipe.*;
import crypticlib.command.BukkitCommand;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;

import java.util.Arrays;

@Command
public class PluginCommand extends BukkitCommand {

    public static final PluginCommand INSTANCE = new PluginCommand();

    PluginCommand() {
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
    BukkitSubcommand run = RunArcencielCmd.INSTANCE;

    @Subcommand
    BukkitSubcommand create = CreateRecipeCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand list = RecipeListCommand.INSTANCE;

    @Subcommand
    BukkitSubcommand display = DisplayRecipeCommand.INSTANCE;

}
