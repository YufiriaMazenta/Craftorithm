package com.github.yufiriamazenta.craftorithm.cmd;

import com.github.yufiriamazenta.craftorithm.cmd.sub.*;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.BukkitCommand;
import crypticlib.command.ISubcmdExecutor;
import crypticlib.command.impl.RootCmdExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BukkitCommand(
        name = "craftorithm",
        aliases = {"craft"},
        permission = "craftorithm.command"
)
public class PluginCommand extends RootCmdExecutor {

    PluginCommand() {
        regDefaultSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.isEmpty()) {
            LangUtil.sendLang(sender, Languages.COMMAND_NOT_ENOUGH_PARAM.value(), CollectionsUtil.newStringHashMap("<number>", String.valueOf(1)));
            return true;
        }
        ISubcmdExecutor subCommand = subcommands().get(argList.get(0));
        if (subCommand != null) {
            String perm = subCommand.permission();
            if (perm != null) {
                if (!sender.hasPermission(perm)) {
                    LangUtil.sendLang(sender, Languages.COMMAND_NO_PERM.value());
                    return true;
                }
            }
            return subCommand.onCommand(sender, argList.subList(1, argList.size()));
        }
        else {
            LangUtil.sendLang(sender, Languages.COMMAND_UNDEFINED_SUBCMD.value());
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
            .regSub(RecipeListCommand.INSTANCE);
    }

}
