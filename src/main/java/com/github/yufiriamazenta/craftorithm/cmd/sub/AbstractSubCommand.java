package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.api.ISubcmdExecutor;
import crypticlib.command.impl.SubcmdExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractSubCommand extends SubcmdExecutor {

    protected AbstractSubCommand(String command, String perm) {
        super(command, perm);
    }

    protected AbstractSubCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        ISubcmdExecutor subCommand = subcommands().get(args.get(0));
        if (subCommand == null) {
            LangUtil.sendLang(sender, "command.undefined_subcmd");
        } else {
            String perm = subCommand.permission();
            if (perm != null) {
                if (!sender.hasPermission(perm)) {
                    LangUtil.sendLang(sender, "command.no_perm");
                    return true;
                }
            }
            subCommand.onCommand(sender, args.subList(1, args.size()));
        }
        return true;
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, int paramNum) {
        sendNotEnoughCmdParamMsg(sender, String.valueOf(paramNum));
    }

    public void sendNotEnoughCmdParamMsg(CommandSender sender, String paramStr) {
        LangUtil.sendLang(sender, "command.not_enough_param", CollectionsUtil.newHashMap("<number>", paramStr));
    }

    public boolean checkSenderIsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            LangUtil.sendLang(sender, "command.player_only");
            return false;
        }
    }

    public void filterTabList(List<String> tabList, String input) {
        tabList.removeIf(str -> !str.startsWith(input));
    }

}
