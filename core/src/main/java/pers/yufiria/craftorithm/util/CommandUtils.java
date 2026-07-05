package pers.yufiria.craftorithm.util;

import crypticlib.command.CommandInvoker;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;

import java.util.List;

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

    /**
     * 从参数列表中解析标志值。例如：对于参数 ["--player", "Steve", "--type", "crafting"]和标志 "--player", 返回 "Steve"。
     *
     */
    public static @Nullable String parseFlag(List<String> args, String flag) {
        for (int i = 0; i < args.size() - 1; i++) {
            if (args.get(i).equalsIgnoreCase(flag)) {
                return args.get(i + 1);
            }
        }
        return null;
    }

    /**
     * 检查参数列表中是否存在某个标志（不需要值）。
     */
    public static boolean hasFlag(List<String> args, String flag) {
        return args.stream().anyMatch(a -> a.equalsIgnoreCase(flag));
    }

}
