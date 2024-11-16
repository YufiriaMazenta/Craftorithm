package com.github.yufiriamazenta.craftorithm.cmd.sub.item;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import crypticlib.util.ItemHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SaveItemCommand extends BukkitSubcommand {

    public static final SaveItemCommand INSTANCE = new SaveItemCommand();

    private SaveItemCommand() {
        super(
            CommandInfo.builder("save").permission(new PermInfo("craftorithm.command.item.save")).build()
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!CommandUtils.checkSenderIsPlayer(sender))
            return;
        if (args.size() < 2) {
            sendDescriptions(sender);
            return;
        }

        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        if (ItemHelper.isAir(item)) {
            LangUtil.sendLang(sender, Languages.COMMAND_ITEM_SAVE_FAILED_SAVE_AIR);
            return;
        }

        CraftorithmItemProvider.INSTANCE.regCraftorithmItem(args.get(0), args.get(1), item.clone());
        LangUtil.sendLang(sender, Languages.COMMAND_ITEM_SAVE_SUCCESS);
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            return new ArrayList<>(CraftorithmItemProvider.INSTANCE.itemConfigFileMap().keySet());
        } else {
            return Collections.singletonList("");
        }
    }
}
