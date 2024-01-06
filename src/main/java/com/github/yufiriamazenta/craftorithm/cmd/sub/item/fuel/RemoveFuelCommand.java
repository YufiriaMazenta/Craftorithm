package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveFuelCommand extends AbstractSubCommand {

    public static final RemoveFuelCommand INSTANCE = new RemoveFuelCommand();

    protected RemoveFuelCommand() {
        super("remove", "craftorithm.command.item.fuel.remove");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        if (!checkSenderIsPlayer(sender))
            return true;

        boolean result = ItemManager.INSTANCE.removeCustomFuel(args.get(0));
        if (result) {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_FUEL_REMOVE_SUCCESS);
        } else {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_FUEL_REMOVE_FAILED_NOT_EXIST);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabs = new ArrayList<>(ItemManager.INSTANCE.customCookingFuelMap().keySet());
            filterTabList(tabs, args.get(0));
            return tabs;
        }
        return Collections.singletonList("");
    }
}
