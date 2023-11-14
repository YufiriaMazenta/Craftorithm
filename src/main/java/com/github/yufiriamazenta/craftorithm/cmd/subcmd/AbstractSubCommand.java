package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubCmdExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSubCommand implements ISubCmdExecutor {

    private final String command;
    private String perm;
    private Map<String, ISubCmdExecutor> subCommandMap;

    protected AbstractSubCommand(String command, Map<String, ISubCmdExecutor> subCommandMap, String perm) {
        this.command = command;
        this.subCommandMap = subCommandMap;
        this.perm = perm;
    }

    protected AbstractSubCommand(String command, String perm) {
        this(command, new ConcurrentHashMap<>(), perm);
    }

    protected AbstractSubCommand(String command) {
        this(command, new ConcurrentHashMap<>(), null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        ISubCmdExecutor subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            LangUtil.sendMsg(sender, "command.undefined_subcmd");
        } else {
            String perm = subCommand.permission();
            if (perm != null) {
                if (!sender.hasPermission(perm)) {
                    LangUtil.sendMsg(sender, "command.no_perm");
                    return true;
                }
            }
            subCommand.onCommand(sender, args.subList(1, args.size()));
        }
        return true;
    }

    @Override
    public String subCommandName() {
        return command;
    }

    @Override
    public @NotNull Map<String, ISubCmdExecutor> subCommands() {
        return subCommandMap;
    }

    public void setSubCommandMap(Map<String, ISubCmdExecutor> subCommandMap) {
        this.subCommandMap = subCommandMap;
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, int paramNum) {
        sendNotEnoughCmdParamMsg(sender, String.valueOf(paramNum));
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, String paramStr) {
        LangUtil.sendMsg(sender, "command.not_enough_param", ContainerUtil.newHashMap("<number>", paramStr));
    }

    public boolean checkSenderIsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            LangUtil.sendMsg(sender, "command.player_only");
            return false;
        }
    }

    public void filterTabList(List<String> tabList, String input) {
        tabList.removeIf(str -> !str.startsWith(input));
    }

    @Override
    public String permission() {
        return perm;
    }

    public void setPerm() {
        this.perm = perm;
    }

}
