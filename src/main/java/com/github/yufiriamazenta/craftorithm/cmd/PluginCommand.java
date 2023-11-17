package com.github.yufiriamazenta.craftorithm.cmd;

import com.github.yufiriamazenta.craftorithm.cmd.sub.*;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.annotations.BukkitCommand;
import crypticlib.command.IPluginCmdExecutor;
import crypticlib.command.ISubCmdExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@BukkitCommand(
        name = "craftorithm",
        alias = {"craft"},
        permission = "craftorithm.command"
)
public enum PluginCommand implements IPluginCmdExecutor {

    INSTANCE;

    private final Map<String, ISubCmdExecutor> subCommandMap;

    PluginCommand() {
        subCommandMap = new ConcurrentHashMap<>();
        regDefaultSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.isEmpty()) {
            LangUtil.sendLang(sender, "command.not_enough_param", ContainerUtil.newHashMap("<number>", String.valueOf(1)));
            return true;
        }
        ISubCmdExecutor subCommand = subCommandMap.get(argList.get(0));
        if (subCommand != null) {
            String perm = subCommand.permission();
            if (perm != null) {
                if (!sender.hasPermission(perm)) {
                    LangUtil.sendLang(sender, "command.no_perm");
                    return true;
                }
            }
            return subCommand.onCommand(sender, argList.subList(1, argList.size()));
        }
        else {
            LangUtil.sendLang(sender, "command.undefined_subcmd");
            return true;
        }
    }

    private void regDefaultSubCommands() {
        regSubCommand(ReloadCommand.INSTANCE);
        regSubCommand(VersionCommand.INSTANCE);
        regSubCommand(RemoveRecipeCommand.INSTANCE);
        regSubCommand(DisableRecipeCommand.INSTANCE);
        regSubCommand(ItemCommand.INSTANCE);
        regSubCommand(RunArcencielCmd.INSTANCE);
        regSubCommand(CreateRecipeCommand.INSTANCE);
        regSubCommand(RecipeListCommand.INSTANCE);
    }

    @Override
    public @NotNull Map<String, ISubCmdExecutor> subCommands() {
        return subCommandMap;
    }

    @Override
    public Plugin getPlugin() {
        return null;
    }

}
