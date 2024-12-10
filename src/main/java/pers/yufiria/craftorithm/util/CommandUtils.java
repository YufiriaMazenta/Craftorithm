package pers.yufiria.craftorithm.util;

import pers.yufiria.craftorithm.config.Languages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUtils {


    public static boolean checkSenderIsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            LangUtils.sendLang(sender, Languages.COMMAND_PLAYER_ONLY);
            return false;
        }
    }

}
