package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.util.ContainerUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSubCommand implements ISubCommand {

    private final String command;
    private String perm;
    private Map<String, ISubCommand> subCommandMap;

    protected AbstractSubCommand(String command, Map<String, ISubCommand> subCommandMap, String perm) {
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
        ISubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            LangUtil.sendMsg(sender, "command.undefined_subcmd");
        } else {
            String perm = subCommand.getPerm();
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
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (subCommandMap == null)
            return Collections.singletonList("");
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (String subCmd : subCommandMap.keySet()) {
                ISubCommand subCommand = subCommandMap.get(subCmd);
                if (subCommand.getPerm() != null) {
                    if (sender.hasPermission(subCommand.getPerm()))
                        tabList.add(subCmd);
                } else {
                    tabList.add(subCmd);
                }
            }
            tabList.removeIf(str -> !str.startsWith(args.get(0)));
            return tabList;
        }
        ISubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand != null) {
            if (subCommand.getPerm() != null) {
                if (!sender.hasPermission(subCommand.getPerm()))
                    return Collections.singletonList("");
            }
            return subCommand.onTabComplete(sender, args.subList(1, args.size()));
        }
        else
            return Collections.singletonList("");
    }

    @Override
    public void setPerm(String perm) {
        this.perm = perm;
    }

    @Override
    public String getSubCommandName() {
        return command;
    }

    @Override
    public Map<String, ISubCommand> getSubCommands() {
        return subCommandMap;
    }

    public void setSubCommandMap(Map<String, ISubCommand> subCommandMap) {
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
    public String getPerm() {
        return perm;
    }

    public void setPerm() {
        this.perm = perm;
    }

}
