package pers.yufiria.craftorithm.command.sub.item;

import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.impl.CraftorithmItemProvider;
import pers.yufiria.craftorithm.util.CollectionsUtils;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;
import crypticlib.CrypticLibBukkit;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import crypticlib.platform.IPlatform;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GiveItemCommand extends BukkitSubcommand {

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
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }

        Player player;
        if (args.size() >= 2) {
            player = Bukkit.getPlayer(args.get(1));
            if (player == null) {
                LangUtils.sendLang(sender, Languages.COMMAND_ITEM_GIVE_PLAYER_OFFLINE);
                return;
            }
        } else {
            if (CommandUtils.checkSenderIsPlayer(sender)) {
                player = (Player) sender;
            } else {
                return;
            }
        }

        ItemStack itemStack = CraftorithmItemProvider.INSTANCE.matchItem(args.get(0));
        if (itemStack == null) {
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_GIVE_NOT_EXIST_ITEM, CollectionsUtils.newStringHashMap("<item_name>", args.get(0)));
            return;
        }

        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);
        if (!failedItems.isEmpty()) {
            if (!CrypticLibBukkit.platform().type().equals(IPlatform.PlatformType.FOLIA)) {
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
        LangUtils.sendLang(sender, Languages.COMMAND_ITEM_GIVE_SUCCESS);
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
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
