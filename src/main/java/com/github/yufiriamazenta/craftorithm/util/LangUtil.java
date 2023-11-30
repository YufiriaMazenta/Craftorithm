package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LangUtil {

    private static final YamlConfigWrapper langConfigFile;
    private static final Map<String, String> defaultFormatMap;

    static {
        langConfigFile = new YamlConfigWrapper(Craftorithm.instance(), "lang.yml");
        Languages.reloadLanguages();
        defaultFormatMap = new HashMap<>();
        defaultFormatMap.put("<prefix>", Languages.prefix.value());
        defaultFormatMap.put("<version>", Craftorithm.instance().getDescription().getVersion());
    }

    public static void sendLang(CommandSender receiver, String msgKey) {
        sendLang(receiver, msgKey, new HashMap<>());
    }

    public static void sendLang(CommandSender receiver, String message, Map<String, String> formatMap) {
        if (receiver == null) {
            return;
        }
        formatMap.putAll(defaultFormatMap);

        for (String formatStr : formatMap.keySet()) {
            message = message.replace(formatStr, formatMap.get(formatStr));
        }
        if (receiver instanceof Player)
            message = TextUtil.placeholder((Player) receiver, message);
        receiver.sendMessage(TextUtil.color(message));
    }

    public static void info(String message) {
        sendLang(Bukkit.getConsoleSender(), message);
    }

    public static void info(String message, Map<String, String> map) {
        sendLang(Bukkit.getConsoleSender(), message, map);
    }

    public static YamlConfigWrapper langConfigFile() {
        return langConfigFile;
    }

}
