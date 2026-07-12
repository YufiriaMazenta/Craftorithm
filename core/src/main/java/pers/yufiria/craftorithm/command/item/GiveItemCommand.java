package pers.yufiria.craftorithm.command.item;

import crypticlib.CrypticLibBukkit;
import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.CraftorithmItemProvider;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GiveItemCommand extends CommandNode {

    public static final GiveItemCommand INSTANCE = new GiveItemCommand();

    private GiveItemCommand() {
        super(
            CommandInfo
                .builder("give")
                .permission(new PermInfo("craftorithm.command.item.give"))
                .usage("&r/craftorithm item give <item_id> [player_name]")
                .build()
        );
    }

    @Override
    public void execute(@NotNull Invoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }

        Player player;
        if (args.size() >= 2) {
            player = Bukkit.getPlayer(args.get(1));
            if (player == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_GIVE_PLAYER_OFFLINE);
                return;
            }
        } else {
            if (CommandUtils.checkInvokerIsPlayer(invoker)) {
                Optional<Player> playerOpt = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer);
                if (playerOpt.isEmpty()) {
                    LangUtils.sendLang(invoker, Languages.COMMAND_PLAYER_ONLY);
                    return;
                }
                player = playerOpt.get();
            } else {
                return;
            }
        }

        ItemStack itemStack = CraftorithmItemProvider.INSTANCE.matchItem(args.get(0));
        if (itemStack == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_GIVE_NOT_EXIST_ITEM, CollectionsUtils.newStringHashMap("<item_name>", args.get(0)));
            return;
        }

        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);
        if (!failedItems.isEmpty()) {
            if (!CrypticLibBukkit.isFolia()) {
                for (ItemStack stack : failedItems.values()) {
                    player.getWorld().dropItem(player.getLocation(), stack);
                }
            } else {
                Runnable dropTask = () -> {
                    for (ItemStack stack : failedItems.values()) {
                        player.getWorld().dropItem(player.getLocation(), stack);
                    }
                };
                CrypticLibBukkit.scheduler().runOnEntity(player, dropTask, dropTask);
            }
        }
        LangUtils.sendLang(invoker, Languages.COMMAND_ITEM_GIVE_SUCCESS);
    }

    @Override
    public List<String> tabComplete(@NotNull Invoker invoker, List<String> args) {
        if (args.size() < 2) {
            return new ArrayList<>(CraftorithmItemProvider.INSTANCE.itemMap().keySet());
        }
        else
            return getOnlinePlayerNameList();
    }

    private List<String> getOnlinePlayerNameList() {
        List<String> onlinePlayerNameList = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayerNameList.add(onlinePlayer.getName());
        }
        return onlinePlayerNameList;
    }

}
