package top.oasismc.oasisrecipe.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.ItemLoader;
import top.oasismc.oasisrecipe.item.nbt.NBTManager;
import top.oasismc.oasisrecipe.recipe.RecipeManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static top.oasismc.oasisrecipe.OasisRecipe.getPlugin;

public enum PluginCommand implements TabExecutor {

    INSTANCE;

    private final List<String> subCommandList;
    private final Map<String, BiConsumer<CommandSender, String[]>> subCommandMap;
    private final Map<String, List<String>> subCommandArgListMap;

    PluginCommand() {
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
        subCommandMap.get(args[0]).accept(sender, args);
        return true;
    }

    private void regDefaultSubCommands() {
        regSubCommand("reload", (sender, args) -> {
            reloadPlugin();
            getPlugin().sendMsg(sender, "commands.reload");
        });
        regSubCommand("version", (sender, args) -> {
            getPlugin().sendMsg(sender, "commands.version");
        });
        regSubCommand("import", (sender, args) -> {
            if (args.length < 3) {
                getPlugin().sendMsg(sender, "commands.missingParam");
                return;
            }
            if (!(sender instanceof Player)) {
                getPlugin().sendMsg(sender, "commands.playerOnly");
                return;
            }
            importItem((Player) sender, args);
            getPlugin().sendMsg(sender, "commands.import");
        }, Arrays.asList("items", "results"));
    }

    public void regSubCommand(String subCommand, BiConsumer<CommandSender, String[]> consumer) {
        regSubCommand(subCommand, consumer, Collections.singletonList(""));
    }

    public void regSubCommand(String subCommand, BiConsumer<CommandSender, String[]> consumer, List<String> args) {
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

    public List<String> getSubCommandList() {
        return subCommandList;
    }

    public Map<String, BiConsumer<CommandSender, String[]>> getSubCommandMap() {
        return subCommandMap;
    }

    public void importItem(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ConfigFile configFile;
        switch (args[1]) {
            case "RESULTS":
            case "results":
                configFile = ItemLoader.getResultFile();
                break;
            case "items":
            case "ITEMS":
            default:
                configFile = ItemLoader.getItemFile();
                break;
        }
        NBTManager.importItem(args[2], item, configFile);
    }

    public void reloadPlugin() {
        getPlugin().reloadConfig();
        ItemLoader.getItemFile().reloadConfig();
        ItemLoader.getResultFile().reloadConfig();
        RecipeManager.INSTANCE.getRecipeFile().reloadConfig();
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            RecipeManager.INSTANCE.reloadRecipes();
        } else {
            if (ItemLoader.INSTANCE.isItemsAdderLoaded()) {
                RecipeManager.INSTANCE.reloadRecipes();
            }
        }
    }

}
