package me.yufiria.craftorithm.cmd.subcmd.item;

import me.yufiria.craftorithm.api.cmd.AbstractSubCommand;
import me.yufiria.craftorithm.api.cmd.ISubCommand;
import me.yufiria.craftorithm.item.ItemManager;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemGiveCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ItemGiveCommand();

    private ItemGiveCommand() {
        super("give");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }

        Player player;
        if (args.size() >= 2) {
            player = Bukkit.getPlayer(args.get(1));
            if (player == null) {
                LangUtil.sendMsg(sender, "command.item.give.player_offline");
                return true;
            }
        } else {
            if (checkSenderIsPlayer(sender)) {
                player = (Player) sender;
            } else {
                LangUtil.sendMsg(sender, "command.player_only");
                return true;
            }
        }

        if (!ItemManager.isCraftorithmItem(args.get(0))) {
            LangUtil.sendMsg(sender, "command.item.give.not_exist_item", MapUtil.newHashMap("<item_name>", args.get(0)));
            return true;
        }

        player.getInventory().addItem(ItemManager.getCraftorithmItem(args.get(0)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            List<String> tabList = new ArrayList<>(ItemManager.getItemMap().keySet());
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
