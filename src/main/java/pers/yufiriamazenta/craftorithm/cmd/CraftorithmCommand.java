package pers.yufiriamazenta.craftorithm.cmd;

import pers.yufiriamazenta.craftorithm.cmd.sub.ReloadCommand;
import pers.yufiriamazenta.craftorithm.cmd.sub.RunArcencielCmd;
import pers.yufiriamazenta.craftorithm.cmd.sub.VersionCommand;
import pers.yufiriamazenta.craftorithm.cmd.sub.item.ItemCommand;
import pers.yufiriamazenta.craftorithm.cmd.sub.recipe.*;
import pers.yufiriamazenta.craftorithm.config.Languages;
import pers.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.BukkitCommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Command
public class CraftorithmCommand extends BukkitCommand {

    public static CraftorithmCommand INSTANCE = new CraftorithmCommand();

    CraftorithmCommand() {
        super(
            new CommandInfo(
                "craftorithm",
                new PermInfo("craftorithm.command"),
                List.of("cra", "crafto"),
                "Craftorithm main command",
                "/craftorithm|cra|crafto [...]"
            )
        );
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        sendDescriptions(sender);
    }

    @Subcommand
    ReloadCommand reload = ReloadCommand.INSTANCE;

    @Subcommand
    VersionCommand version = VersionCommand.INSTANCE;

    @Subcommand
    RemoveRecipeCommand remove = RemoveRecipeCommand.INSTANCE;

    @Subcommand
    ItemCommand item = ItemCommand.INSTANCE;

    @Subcommand
    RunArcencielCmd runArcenciel = RunArcencielCmd.INSTANCE;

    @Subcommand
    DisplayRecipeCommand display = DisplayRecipeCommand.INSTANCE;

    @Subcommand
    DisableRecipeCommand disable = DisableRecipeCommand.INSTANCE;

    @Subcommand
    RecipeListCommand recipeList = RecipeListCommand.INSTANCE;

    @Subcommand
    CreateRecipeCommand create = CreateRecipeCommand.INSTANCE;

}
