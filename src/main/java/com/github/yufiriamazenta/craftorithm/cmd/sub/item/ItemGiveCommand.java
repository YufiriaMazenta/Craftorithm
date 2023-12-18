package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.util.CollectionsUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubcmdExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemGiveCommand extends AbstractSubCommand {

    public static final ISubcmdExecutor INSTANCE = new ItemGiveCommand();

    private ItemGiveCommand() {
        super("give");
    }

    @Override
    public Boolean onCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }

        Player player;
        if (args.size() >= 2) {
            player = Bukkit.getPlayer(args.get(1));
            if (player == null) {
                LangUtil.sendLang(sender, Languages.COMMAND_ITEM_GIVE_PLAYER_OFFLINE);
                return true;
            }
        } else {
            if (checkSenderIsPlayer(sender)) {
                player = (Player) sender;
            } else {
                return true;
            }
        }

        ItemStack itemStack = CraftorithmItemProvider.INSTANCE.getItem(args.get(0));
        if (itemStack == null) {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_GIVE_NOT_EXIST_ITEM, CollectionsUtil.newStringHashMap("<item_name>", args.get(0)));
            return true;
        }

        player.getInventory().addItem(itemStack);
        LangUtil.sendLang(sender, Languages.COMMAND_ITEM_GIVE_SUCCESS);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            List<String> tabList = new ArrayList<>(CraftorithmItemProvider.INSTANCE.itemMap().keySet());
            filterTabList(tabList, args.get(0));
            return tabList;
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
