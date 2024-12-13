package pers.yufiria.craftorithm.command.sub.item.fuel;

import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;
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

        boolean result = ItemManager.INSTANCE.removeCustomFuel(NamespacedItemId.fromString(args.get(0)));
        if (result) {
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_FUEL_REMOVE_SUCCESS);
        } else {
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_FUEL_REMOVE_FAILED_NOT_EXIST);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(ItemManager.INSTANCE.customCookingFuelMap().keySet().stream().map(NamespacedItemId::toString).toList());
        }
        return Collections.singletonList("");
    }
}
