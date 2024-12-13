package pers.yufiria.craftorithm.command.sub.item;

import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.impl.CraftorithmItemProvider;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;
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
            CommandInfo
                .builder("save")
                .permission(new PermInfo("craftorithm.command.item.save"))
                .usage("&r/craftorithm item save <file_name> <item_id>")
                .build()
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
            LangUtils.sendLang(sender, Languages.COMMAND_ITEM_SAVE_FAILED_SAVE_AIR);
            return;
        }

        CraftorithmItemProvider.INSTANCE.regCraftorithmItem(args.get(0), args.get(1), item.clone());
        LangUtils.sendLang(sender, Languages.COMMAND_ITEM_SAVE_SUCCESS);
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
