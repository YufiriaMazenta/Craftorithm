package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.item.ItemManager;
import top.oasismc.oasisrecipe.util.MapUtil;
import top.oasismc.oasisrecipe.util.MsgUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemGetCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ItemGetCommand();

    private ItemGetCommand() {
        super("get", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        if (!checkSenderIsPlayer(sender))
            return true;

        if (!ItemManager.isOasisRecipeItem(args.get(0))) {
            MsgUtil.sendMsg(sender, "command.item.get.not_exist_item", MapUtil.getHashMap("<item_name>", args.get(0)));
            return true;
        }

        ((Player) sender).getInventory().addItem(ItemManager.getOasisRecipeItem(args.get(0)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>(ItemManager.getItemMap().keySet());
    }

}
