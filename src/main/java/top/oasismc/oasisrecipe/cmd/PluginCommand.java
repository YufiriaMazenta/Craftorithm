package top.oasismc.oasisrecipe.cmd;

import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.ItemUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static top.oasismc.oasisrecipe.OasisRecipe.getPlugin;
import static top.oasismc.oasisrecipe.recipe.RecipeManager.getManager;

public class PluginCommand implements TabExecutor {

    private final List<String> subCommandList;
    private final Map<String, BiConsumer<CommandSender, String[]>> subCommandMap;
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

    public static PluginCommand getCommand() {
        return command;
    }

    public List<String> getSubCommandList() {
        return subCommandList;
    }

    public Map<String, BiConsumer<CommandSender, String[]>> getSubCommandMap() {
        return subCommandMap;
    }

    public void importItem(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ConfigFile config = ItemUtil.getItemFile();
            switch (args[1]) {
                case "RESULTS":
                case "results":
                    config = ItemUtil.getResultFile();
                    List<String> attributes = new ArrayList<>();
                    Multimap<Attribute, AttributeModifier> attrMap = meta.getAttributeModifiers();
                    if (attrMap != null) {
                        attrMap.forEach((attr, attrModifier) -> {
                            String attrStr = "";
                            attrStr += attr.name() + " ";
                            attrStr += attrModifier.getAmount() + " ";
                            attrStr += attrModifier.getOperation() + " ";
                            attrStr += attrModifier.getSlot();
                            attributes.add(attrStr);
                        });
                        config.getConfig().set(args[2] + ".attributes", attributes);
                    }
                    config.getConfig().set(args[2] + ".customModelData", meta.getCustomModelData());
                    break;
            }
            config.getConfig().set(args[2] + ".material", item.getType().name());
            config.getConfig().set(args[2] + ".amount", item.getAmount());
            if (meta.hasDisplayName())
                config.getConfig().set(args[2] + ".name", meta.getDisplayName());
            config.getConfig().set(args[2] + ".unbreakable", meta.isUnbreakable());
            List<String> enchants = new ArrayList<>();
            if (meta.hasEnchants()) {
                meta.getEnchants().forEach((enchant, lvl) -> {
                    String type = enchant.toString();
                    type = type.substring(type.indexOf(", ") + 2, type.length() - 1);
                    enchants.add(type + " " + lvl);
                });
            }
            config.getConfig().set(args[2] + ".enchants", enchants);
            List<String> lore = meta.getLore();
            if (lore != null)
                config.getConfig().set(args[2] + ".lore", lore);
            List<String> flags = new ArrayList<>();
            for (ItemFlag flag : meta.getItemFlags()) {
                flags.add(flag.name().substring(5));
            }
            if (flags.size() != 0)
                config.getConfig().set(args[2] + ".hides", flags);
            if (meta instanceof Damageable)
                config.getConfig().set(args[2] + ".durability", ((Damageable) meta).getDamage());
            config.saveConfig();
        }
    }

    public void reloadPlugin() {
        getPlugin().reloadConfig();
        ItemUtil.getItemFile().reloadConfig();
        ItemUtil.getResultFile().reloadConfig();
        getManager().getRecipeFile().reloadConfig();
        getManager().reloadRecipes();
    }

}
