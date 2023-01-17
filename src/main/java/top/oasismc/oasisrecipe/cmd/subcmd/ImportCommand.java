package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.ItemUtil;

import java.util.List;

public final class ImportCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ImportCommand();

    private ImportCommand() {
        super("import", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            OasisRecipe.getInstance().sendMsg(sender, "commands.missingParam");
            return true;
        }
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        ConfigFile configFile = new ConfigFile("items.yml");
        ItemUtil.saveItem2Config(item, configFile, args.get(0) + ".nbt");
        OasisRecipe.getInstance().sendMsg(sender, "commands.import");
        return true;
    }
}
