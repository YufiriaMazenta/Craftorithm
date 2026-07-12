package pers.yufiria.craftorithm.command.item.fuel;

import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AddFuelCommand extends CommandNode {

    public static final AddFuelCommand INSTANCE = new AddFuelCommand();

    protected AddFuelCommand() {
        super(CommandInfo.builder("add").build());
    }

    @Override
    public void execute(@NotNull Invoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        if (!CommandUtils.checkInvokerIsPlayer(invoker))
            return;

        Optional<Player> playerOpt = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer);
        if (playerOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_PLAYER_ONLY);
            return;
        }
        ItemStack item = playerOpt.get().getInventory().getItemInMainHand();
        if (ItemHelper.isAir(item)) {
            LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_FUEL_ADD_FAILED_ADD_AIR);
            return;
        }

        boolean result = ItemManager.INSTANCE.addCustomFuel(item, Integer.parseInt(args.getFirst()));
        if (result) {
            LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_FUEL_ADD_SUCCESS);
        } else {
            LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_FUEL_ADD_FAILED_EXIST);
        }
    }

    @Override
    public List<String> tabComplete(@NotNull Invoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(Arrays.asList("50", "67", "100", "150", "200", "300", "800", "1200", "1600", "2400", "4001", "16000", "20000"));
        }
        return Collections.singletonList("");
    }
}
