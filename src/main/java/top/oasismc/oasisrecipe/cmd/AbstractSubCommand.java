package top.oasismc.oasisrecipe.cmd;

import org.bukkit.command.CommandSender;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.ISubCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractSubCommand implements ISubCommand {

    private final String command;
    private Map<String, ISubCommand> subCommandMap;

    public AbstractSubCommand(String command, Map<String, ISubCommand> subCommandMap) {
        this.command = command;
        this.subCommandMap = subCommandMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (subCommandMap == null || args.size() < 1) {
            return true;
        }
        ISubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            OasisRecipe.getPlugin().sendMsg(sender, "command.missingSubCmd");
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
            List<String> returnList = new ArrayList<>(subCommandMap.keySet());
            returnList.removeIf(str -> !str.startsWith(args.get(0)));
            return returnList;
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

}
