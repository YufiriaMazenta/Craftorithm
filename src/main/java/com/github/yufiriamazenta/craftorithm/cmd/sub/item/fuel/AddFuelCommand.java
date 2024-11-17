package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.util.ItemHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddFuelCommand extends BukkitSubcommand {

    public static final AddFuelCommand INSTANCE = new AddFuelCommand();

    protected AddFuelCommand() {
        super(CommandInfo.builder("add").build());
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        if (!CommandUtils.checkSenderIsPlayer(sender))
            return;

        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        if (ItemHelper.isAir(item)) {
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_FUEL_ADD_FAILED_ADD_AIR);
            return;
        }

        boolean result = ItemManager.INSTANCE.addCustomFuel(item, Integer.parseInt(args.get(0)));
        if (result) {
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_FUEL_ADD_SUCCESS);
        } else {
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_FUEL_ADD_FAILED_EXIST);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(Arrays.asList("50", "67", "100", "150", "200", "300", "800", "1200", "1600", "2400", "4001", "16000", "20000"));
        }
        return Collections.singletonList("");
    }
}
