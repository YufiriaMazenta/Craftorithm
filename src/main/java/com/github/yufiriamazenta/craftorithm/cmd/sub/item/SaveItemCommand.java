package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.util.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SaveItemCommand extends AbstractSubCommand {

    public static final SaveItemCommand INSTANCE = new SaveItemCommand();

    private SaveItemCommand() {
        super("save", "craftorithm.command.item.save");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        if (!checkSenderIsPlayer(sender))
            return true;

        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        if (ItemUtil.isAir(item)) {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_SAVE_FAILED_SAVE_AIR);
            return true;
        }

        CraftorithmItemProvider.INSTANCE.regCraftorithmItem(args.get(0), args.get(1), item);
        LangUtil.sendLang(sender, Languages.COMMAND_ITEM_SAVE_SUCCESS);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            List<String> tabList = new ArrayList<>(CraftorithmItemProvider.INSTANCE.itemConfigFileMap().keySet());
            filterTabList(tabList, args.get(0));
            return tabList;
        } else {
            return Collections.singletonList("");
        }
    }
}
