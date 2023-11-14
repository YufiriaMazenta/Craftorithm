package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import crypticlib.CrypticLib;
import crypticlib.config.impl.YamlConfigWrapper;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class LangUtil {

    private static final Pattern colorPattern = Pattern.compile("&#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
    private static final YamlConfigWrapper langConfigFile = new YamlConfigWrapper(Craftorithm.getInstance(), "lang.yml");
    private static final Map<String, String> defaultFormatMap;

    static {
        defaultFormatMap = new HashMap<>();
        defaultFormatMap.put("<prefix>", langConfigFile.config().getString("prefix", "&8[&3Oasis&bRecipe&8]"));
        defaultFormatMap.put("<version>", Craftorithm.getInstance().getDescription().getVersion());
    }

    public static void sendMsg(CommandSender sender, String msgKey) {
        sendMsg(sender, msgKey, new HashMap<>());
    }

    public static void sendMsg(CommandSender sender, String msgKey, Map<String, String> formatMap) {
        if (sender == null) {
            return;
        }
        formatMap.putAll(defaultFormatMap);
        String message = langConfigFile.config().getString(msgKey, msgKey);

        if (!langConfigFile.config().contains(msgKey)) {
            langConfigFile.config().set(msgKey, msgKey);
            langConfigFile.saveConfig();
            langConfigFile.reloadConfig();
        }

        for (String formatStr : formatMap.keySet()) {
            message = message.replace(formatStr, formatMap.get(formatStr));
        }
        if (sender instanceof Player)
            message = placeholder((Player) sender, message);
        sender.sendMessage(color(message));
    }

    public static String color(String text) {
        if (CrypticLib.minecraftVersion() >= 11600) {
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

    public static void info(String msgKey) {
        sendMsg(Bukkit.getConsoleSender(), msgKey);
    }

    public static void reloadMsgConfig() {
        langConfigFile.reloadConfig();
    }

    public static void info(String msgKey, Map<String, String> map) {
        sendMsg(Bukkit.getConsoleSender(), msgKey, map);
    }

    public static String lang(String key) {
        return langConfigFile.config().getString(key, key);
    }

    public static String placeholder(Player player, String source) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            source = PlaceholderAPI.setPlaceholders(player, source);
        return source;
    }

    public static YamlConfigWrapper getLangConfigFile() {
        return langConfigFile;
    }

}
