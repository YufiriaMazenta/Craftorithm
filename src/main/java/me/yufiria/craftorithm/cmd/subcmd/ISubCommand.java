package me.yufiria.craftorithm.cmd.subcmd;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public interface ISubCommand {

    boolean onCommand(CommandSender sender, List<String> args);

    List<String> onTabComplete(CommandSender sender, List<String> args);

    String getSubCommand();

    Map<String, ISubCommand> getSubCommands();

    void regSubCommand(ISubCommand subCommand);

}
