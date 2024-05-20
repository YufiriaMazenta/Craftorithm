package com.github.yufiriamazenta.craftorithm.cmd;

import com.github.yufiriamazenta.craftorithm.cmd.sub.ReloadCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.RunArcencielCmd;
import com.github.yufiriamazenta.craftorithm.cmd.sub.VersionCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.item.ItemCommand;
import com.github.yufiriamazenta.craftorithm.cmd.sub.recipe.*;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.CommandHandler;
import crypticlib.command.CommandInfo;
import crypticlib.command.SubcommandHandler;
import crypticlib.perm.PermInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@crypticlib.command.annotation.Command
public class PluginCommand extends CommandHandler {

    PluginCommand() {
        super(new CommandInfo("craftorithm", new PermInfo("craftorithm.command"), new String[]{"craft", "cra", "crafto"}));
        regDefaultSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.isEmpty()) {
            LangUtil.sendLang(sender, Languages.COMMAND_NOT_ENOUGH_PARAM, CollectionsUtil.newStringHashMap("<number>", String.valueOf(1)));
            return true;
        }
        SubcommandHandler subCommand = subcommands().get(argList.get(0));
        if (subCommand != null) {
            PermInfo perm = subCommand.permission();
            if (perm != null) {
                if (!sender.hasPermission(perm.permission())) {
                    LangUtil.sendLang(sender, Languages.COMMAND_NO_PERM);
                    return true;
                }
            }
            return subCommand.onCommand(sender, argList.subList(1, argList.size()));
        }
        else {
            LangUtil.sendLang(sender, Languages.COMMAND_UNDEFINED_SUBCMD);
            return true;
        }
    }

    private void regDefaultSubCommands() {
        regSub(ReloadCommand.INSTANCE)
            .regSub(VersionCommand.INSTANCE)
            .regSub(RemoveRecipeCommand.INSTANCE)
            .regSub(DisableRecipeCommand.INSTANCE)
            .regSub(ItemCommand.INSTANCE)
            .regSub(RunArcencielCmd.INSTANCE)
            .regSub(CreateRecipeCommand.INSTANCE)
            .regSub(RecipeListCommand.INSTANCE)
            .regSub(DisplayRecipeCommand.INSTANCE);
    }

}
