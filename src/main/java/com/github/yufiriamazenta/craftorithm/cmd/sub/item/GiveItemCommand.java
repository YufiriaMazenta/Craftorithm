package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtils;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.CrypticLibBukkit;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import crypticlib.platform.Platform;
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

        ItemStack itemStack = CraftorithmItemProvider.INSTANCE.getItem(args.get(0));
        if (itemStack == null) {
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_GIVE_NOT_EXIST_ITEM, CollectionsUtils.newStringHashMap("<item_name>", args.get(0)));
            return;
        }

        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);
        if (!failedItems.isEmpty()) {
            if (!CrypticLibBukkit.platform().type().equals(Platform.PlatformType.FOLIA)) {
                for (ItemStack stack : failedItems.values()) {
                    player.getWorld().dropItem(player.getLocation(), stack);
                }
            } else {
                Runnable dropTask = () -> {
                    for (ItemStack stack : failedItems.values()) {
                        player.getWorld().dropItem(player.getLocation(), stack);
                    }
                };
                CrypticLibBukkit.scheduler().runTaskOnEntity(Craftorithm.instance(), player, dropTask, dropTask);
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
