package com.github.yufiriamazenta.craftorithm.cmd;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.cmd.subcmd.*;
import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.annotations.BukkitCommand;
import crypticlib.command.IPluginCmdExecutor;
import crypticlib.command.ISubCmdExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.*;
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.size() < 1) {
            LangUtil.sendMsg(sender, "command.not_enough_param", ContainerUtil.newHashMap("<number>", String.valueOf(1)));
            return true;
        }
        ISubCmdExecutor subCommand = subCommandMap.get(argList.get(0));
        if (subCommand != null) {
            String perm = subCommand.permission();
            if (perm != null) {
                if (!sender.hasPermission(perm)) {
                    LangUtil.sendMsg(sender, "command.no_perm");
                    return true;
                }
            }
            return subCommand.onCommand(sender, argList.subList(1, argList.size()));
        }
        else {
            LangUtil.sendMsg(sender, "command.undefined_subcmd");
            return true;
        }
    }

    private void regDefaultSubCommands() {
        regSubCommand(ReloadCommand.INSTANCE);
        regSubCommand(VersionCommand.INSTANCE);
        regSubCommand(RemoveRecipeCommand.INSTANCE);
        regSubCommand(ItemCommand.INSTANCE);
        regSubCommand(RunArcencielCmd.INSTANCE);
        regSubCommand(LookRecipeCommand.INSTANCE);
        regSubCommand(CreateRecipeCommand.INSTANCE);
    }

    @Override
    public Plugin getPlugin() {
        return Craftorithm.getInstance();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (String subCmd : subCommandMap.keySet()) {
                ISubCmdExecutor subCommand = subCommandMap.get(subCmd);
                if (sender.hasPermission(subCommand.permission()))
                    tabList.add(subCmd);
            }
            tabList.removeIf(str -> !str.startsWith(args[0]));
            return tabList;
        }
        ISubCmdExecutor subCommand = subCommandMap.get(argList.get(0));
        if (subCommand != null) {
            if (!sender.hasPermission(subCommand.permission()))
                return Collections.singletonList("");
            return subCommand.onTabComplete(sender, argList.subList(1, argList.size()));
        }
        else
            return Collections.singletonList("");
    }

    public Map<String, ISubCmdExecutor> getSubCommandMap() {
        return subCommandMap;
    }


}
