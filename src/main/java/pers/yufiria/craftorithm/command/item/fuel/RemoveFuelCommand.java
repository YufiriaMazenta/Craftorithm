package pers.yufiria.craftorithm.command.item.fuel;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveFuelCommand extends CommandNode {

    public static final RemoveFuelCommand INSTANCE = new RemoveFuelCommand();

    protected RemoveFuelCommand() {
        super(CommandInfo.builder("remove").build());
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        if (!CommandUtils.checkInvokerIsPlayer(invoker))
            return;

        boolean result = ItemManager.INSTANCE.removeCustomFuel(NamespacedItemId.fromString(args.get(0)));
        if (result) {
            LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_FUEL_REMOVE_SUCCESS);
        } else {
            LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_FUEL_REMOVE_FAILED_NOT_EXIST);
        }
    }

    @Override
    public List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(ItemManager.INSTANCE.customCookingFuelMap().keySet().stream().map(NamespacedItemId::toString).toList());
        }
        return Collections.singletonList("");
    }
}
