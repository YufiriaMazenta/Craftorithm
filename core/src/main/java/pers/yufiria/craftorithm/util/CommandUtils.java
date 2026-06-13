package pers.yufiria.craftorithm.util;

import crypticlib.command.CommandInvoker;
import org.bukkit.command.CommandSender;
import pers.yufiria.craftorithm.config.Languages;

public class CommandUtils {


    public static boolean checkInvokerIsPlayer(CommandInvoker invoker) {
        if (invoker.isPlayer()) {
            return true;
        } else {
            LangUtils.sendLang(invoker2Sender(invoker), Languages.COMMAND_PLAYER_ONLY);
            return false;
        }
    }

    public static CommandSender invoker2Sender(CommandInvoker invoker) {
        return (CommandSender) invoker.getPlatformInvoker();
    }

}
