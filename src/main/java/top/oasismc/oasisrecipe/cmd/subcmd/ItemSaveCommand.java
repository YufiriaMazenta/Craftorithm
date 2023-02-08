package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.api.cmd.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;
import top.oasismc.oasisrecipe.item.ItemManager;
import top.oasismc.oasisrecipe.util.ItemUtil;
import top.oasismc.oasisrecipe.util.MsgUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemSaveCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ItemSaveCommand();

    private ItemSaveCommand() {
        super("save", null);
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
        if (item.getType().equals(Material.AIR)) {
            MsgUtil.sendMsg(sender, "command.item.save.failed_save_air");
            return true;
        }

        String itemFileName = args.get(0);
        YamlFileWrapper yamlFileWrapper;
        if (!ItemManager.getItemFileMap().containsKey(itemFileName)) {
            File itemFile = new File(ItemManager.getItemFileFolder(), itemFileName + ".yml");
            if (!itemFile.exists()) {
                try {
                    itemFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            yamlFileWrapper = new YamlFileWrapper(itemFile);
            ItemManager.addRecipeFile(itemFileName, yamlFileWrapper);
        } else {
            yamlFileWrapper = ItemManager.getItemFileMap().get(itemFileName);
        }

        ItemUtil.saveItem2Config(item, yamlFileWrapper, args.get(1));
        ItemManager.addOasisRecipeItem(itemFileName + ":" + args.get(1), item.clone());
        MsgUtil.sendMsg(sender, "command.item.save.success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        switch (args.size()) {
            case 0:
            case 1:
                return new ArrayList<>(ItemManager.getItemFileMap().keySet());
            case 2:
            default:
                return Collections.singletonList("");
        }
    }
}
