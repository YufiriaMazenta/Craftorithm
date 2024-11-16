package com.github.yufiriamazenta.craftorithm.cmd.sub.item.fuel;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveFuelCommand extends BukkitSubcommand {

    public static final RemoveFuelCommand INSTANCE = new RemoveFuelCommand();

    protected RemoveFuelCommand() {
        super(CommandInfo.builder("remove").build());
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        if (!CommandUtils.checkSenderIsPlayer(sender))
            return;

        boolean result = ItemManager.INSTANCE.removeCustomFuel(args.get(0));
        if (result) {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_FUEL_REMOVE_SUCCESS);
        } else {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_FUEL_REMOVE_FAILED_NOT_EXIST);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(ItemManager.INSTANCE.customCookingFuelMap().keySet());
        }
        return Collections.singletonList("");
    }
}
