package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import crypticlib.chat.MessageSender;
import crypticlib.lang.entry.StringLangEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LangUtil {

    public static void sendLang(CommandSender receiver, StringLangEntry message) {
        sendLang(receiver, message, new HashMap<>());
    }

    public static void sendLang(CommandSender receiver, StringLangEntry message, Map<String, String> formatMap) {
        if (receiver == null) {
            return;
        }
        String prefix;
        if (receiver instanceof Player)
            prefix = Languages.PREFIX.value((Player) receiver);
        else
            prefix = Languages.PREFIX.value();
        formatMap.put("<prefix>", prefix);
        formatMap.put("<version>", Craftorithm.instance().getDescription().getVersion());
        MessageSender.sendMsg(receiver, message, formatMap);
    }

    public static void info(StringLangEntry message) {
        sendLang(Bukkit.getConsoleSender(), message);
    }

    public static void info(StringLangEntry message, Map<String, String> map) {
        sendLang(Bukkit.getConsoleSender(), message, map);
    }

}
