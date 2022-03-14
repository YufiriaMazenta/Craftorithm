package top.oasismc.oasisrecipe.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.item.ItemUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static top.oasismc.oasisrecipe.OasisRecipe.getPlugin;
import static top.oasismc.oasisrecipe.recipe.RecipeManager.getManager;

public class PluginCommand implements TabExecutor {

    private final List<String> subCommandList;
    private final Map<String, Consumer<CommandSender>> subCommandMap;
    private final Map<String, List<String>> subCommandArgListMap;
    private static final PluginCommand command;

    static {
        command = new PluginCommand();
    }

    private PluginCommand() {
        subCommandList = new ArrayList<>();
        subCommandMap = new ConcurrentHashMap<>();
        subCommandArgListMap = new ConcurrentHashMap<>();
        regDefaultSubCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("oasis.command.oasisrecipe")) {
            getPlugin().sendMsg(sender, "commands.noPerm");
            return true;
        }
        if (args.length == 0) {
            getPlugin().sendMsg(sender, "commands.noArgs");
            return true;
        }
        if (!subCommandList.contains(args[0])) {
            getPlugin().sendMsg(sender, "commands.nullArg");
            return true;
        }
        subCommandMap.get(args[0]).accept(sender);
        return true;
    }

    private void regDefaultSubCommands() {
        regSubCommand("reload", sender -> {
            reloadPlugin();
            getPlugin().sendMsg(sender, "commands.reload");
        });
        regSubCommand("version", sender -> {
            getPlugin().sendMsg(sender, "commands.version");
        });
//        regSubCommand("import", sender -> {
//            //TODO
//        });
    }

    public void regSubCommand(String subCommand, Consumer<CommandSender> consumer) {
        regSubCommand(subCommand, consumer, Collections.singletonList(""));
    }

    public void regSubCommand(String subCommand, Consumer<CommandSender> consumer, List<String> args) {
        subCommandList.add(subCommand);
        subCommandMap.put(subCommand, consumer);
        subCommandArgListMap.put(subCommand, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("oasis.command.oasisrecipe"))
            return Collections.singletonList("");

        if (args.length == 1)
            return subCommandList;
        else if (args.length == 2)
            return subCommandArgListMap.get(args[0]);
        else
            return Collections.singletonList("");
    }

    public static PluginCommand getCommand() {
        return command;
    }

    public List<String> getSubCommandList() {
        return subCommandList;
    }

    public Map<String, Consumer<CommandSender>> getSubCommandMap() {
        return subCommandMap;
    }

    public void reloadPlugin() {
        getPlugin().reloadConfig();
        ItemUtil.getItemFile().reloadConfig();
        getManager().getRecipeFile().reloadConfig();
        getManager().reloadRecipes();
    }

}
