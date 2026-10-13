package pers.yufiria.craftorithm.util;

import crypticlib.Invoker;
import crypticlib.lang.node.StringLang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;

import java.util.HashMap;
import java.util.Map;

public class LangUtils {

    public static void sendLang(Invoker invoker, StringLang message) {
        sendLang(CommandUtils.invoker2Sender(invoker), message);
    }

    public static void sendLang(CommandSender receiver, StringLang message) {
        sendLang(receiver, message, new HashMap<>());
    }

    public static void sendLang(Invoker invoker, StringLang message, Map<String, String> formatMap) {
        sendLang(CommandUtils.invoker2Sender(invoker), message, formatMap);
    }

    public static void sendLang(CommandSender receiver, StringLang message, Map<String, String> formatMap) {
        if (receiver == null) {
            return;
        }
        formatMap = new HashMap<>(formatMap);
        String prefix;
        if (receiver instanceof Player)
            prefix = Languages.PREFIX.value((Player) receiver);
        else
            prefix = Languages.PREFIX.value();
        formatMap.put("<prefix>", prefix);
        formatMap.put("<version>", Craftorithm.instance().getDescription().getVersion());
        message.send(receiver, formatMap);
    }

    public static void info(StringLang message) {
        sendLang(Bukkit.getConsoleSender(), message);
    }

    public static void info(StringLang message, Map<String, String> map) {
        sendLang(Bukkit.getConsoleSender(), message, map);
    }

}
