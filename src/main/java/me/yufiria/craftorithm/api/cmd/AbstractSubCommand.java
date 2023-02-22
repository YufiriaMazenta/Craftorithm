package me.yufiria.craftorithm.api.cmd;

import me.yufiria.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.yufiria.craftorithm.util.MapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSubCommand implements ISubCommand {

    private final String command;
    private Map<String, ISubCommand> subCommandMap;

    protected AbstractSubCommand(String command, Map<String, ISubCommand> subCommandMap) {
        this.command = command;
        this.subCommandMap = subCommandMap;
    }

    protected AbstractSubCommand(String command) {
        this(command, new ConcurrentHashMap<>());
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        ISubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            LangUtil.sendMsg(sender, "command.undefined_subcmd");
        } else {
            subCommand.onCommand(sender, args.subList(1, args.size()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (subCommandMap == null)
            return Collections.singletonList("");
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>(subCommandMap.keySet());
            filterTabList(tabList, args.get(0));
            return tabList;
        }
        ISubCommand subCmd = subCommandMap.get(args.get(0));
        if (subCmd != null)
            return subCommandMap.get(args.get(0)).onTabComplete(sender, args.subList(1, args.size()));
        return Collections.singletonList("");
    }

    @Override
    public String getSubCommand() {
        return command;
    }

    @Override
    public Map<String, ISubCommand> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
    }

    @Override
    public void regSubCommand(ISubCommand command) {
        if (subCommandMap == null) {
            subCommandMap = new ConcurrentHashMap<>();
        }
        subCommandMap.put(command.getSubCommand(), command);
    }

    public void setSubCommandMap(Map<String, ISubCommand> subCommandMap) {
        this.subCommandMap = subCommandMap;
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, int paramNum) {
        sendNotEnoughCmdParamMsg(sender, String.valueOf(paramNum));
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, String paramStr) {
        LangUtil.sendMsg(sender, "command.not_enough_param", MapUtil.newHashMap("<number>", paramStr));
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

}
