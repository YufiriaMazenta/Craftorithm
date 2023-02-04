package top.oasismc.oasisrecipe.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class MsgUtil {

    private static final Pattern colorPattern = Pattern.compile("&#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
    private static final YamlFileWrapper msgConfigFile = new YamlFileWrapper("messages.yml");
    private static final Map<String, String> defaultFormatMap;

    static {
        defaultFormatMap = new HashMap<>();
        defaultFormatMap.put("<prefix>", msgConfigFile.getConfig().getString("prefix", "&8[&3Oasis&bRecipe&8]"));
        defaultFormatMap.put("<version>", OasisRecipe.getInstance().getDescription().getVersion());
    }

    public static void sendMsg(CommandSender sender, String msgKey) {
        sendMsg(sender, msgKey, new HashMap<>());
    }

    public static void sendMsg(CommandSender sender, String msgKey, Map<String, String> formatMap) {
        if (sender == null) {
            return;
        }
        formatMap.putAll(defaultFormatMap);
        String message = msgConfigFile.getConfig().getString(msgKey, msgKey);
        for (String formatStr : formatMap.keySet()) {
            message = String.format(formatStr, formatMap.get(formatStr));
        }
        if (sender instanceof Player)
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                message = PlaceholderAPI.setPlaceholders((Player) sender, message);
        sender.sendMessage(color(message));
    }

    public static String color(String text) {
        if (OasisRecipe.getInstance().getVanillaVersion() >= 16) {
            StringBuilder strBuilder = new StringBuilder(text);
            Matcher matcher = colorPattern.matcher(strBuilder);
            while (matcher.find()) {
                String colorCode = matcher.group();
                String colorStr = ChatColor.of(colorCode.substring(1)).toString();
                strBuilder.replace(matcher.start(), matcher.start() + colorCode.length(), colorStr);
                matcher = colorPattern.matcher(strBuilder);
            }
            text = strBuilder.toString();
        }
        return translateAlternateColorCodes('&', text);
    }

    public static void info(String text) {
        Bukkit.getConsoleSender().sendMessage(color("&8[&3Oasis&bRecipe&8] &bINFO &8| &r" + text));
    }

    public static Pattern getColorPattern() { return colorPattern; }

}
