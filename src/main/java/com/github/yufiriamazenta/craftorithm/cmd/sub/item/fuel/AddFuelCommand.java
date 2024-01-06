package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.util.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddFuelCommand extends AbstractSubCommand {

    public static final AddFuelCommand INSTANCE = new AddFuelCommand();

    protected AddFuelCommand() {
        super("add", "craftorithm.command.item.fuel.add");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        if (!checkSenderIsPlayer(sender))
            return true;

        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        if (ItemUtil.isAir(item)) {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_FUEL_ADD_FAILED_ADD_AIR);
            return true;
        }

        boolean result = ItemManager.INSTANCE.addCustomFuel(item, Integer.parseInt(args.get(0)));
        if (result) {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_FUEL_ADD_SUCCESS);
        } else {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_FUEL_ADD_FAILED_EXIST);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabs = new ArrayList<>(Arrays.asList("50", "67", "100", "150", "200", "300", "800", "1200", "1600", "2400", "4001", "16000", "20000"));
            filterTabList(tabs, args.get(0));
            return tabs;
        }
        return Collections.singletonList("");
    }
}
