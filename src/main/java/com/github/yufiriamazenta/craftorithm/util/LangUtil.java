package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LangUtil {

    private static final YamlConfigWrapper langConfigFile = new YamlConfigWrapper(Craftorithm.getInstance(), "lang.yml");
    private static final Map<String, String> defaultFormatMap;

    static {
        defaultFormatMap = new HashMap<>();
        defaultFormatMap.put("<prefix>", langConfigFile.config().getString("prefix", "&8[&3Oasis&bRecipe&8]"));
        defaultFormatMap.put("<version>", Craftorithm.getInstance().getDescription().getVersion());
    }

    public static void sendLang(CommandSender sender, String msgKey) {
        sendLang(sender, msgKey, new HashMap<>());
    }

    public static void sendLang(CommandSender sender, String msgKey, Map<String, String> formatMap) {
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
        sender.sendMessage(TextUtil.color(message));
    }

    public static void info(String msgKey) {
        sendLang(Bukkit.getConsoleSender(), msgKey);
    }

    public static void reloadLangConfig() {
        langConfigFile.reloadConfig();
    }

    public static void info(String msgKey, Map<String, String> map) {
        sendLang(Bukkit.getConsoleSender(), msgKey, map);
    }

    public static String langMsg(String key) {
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
