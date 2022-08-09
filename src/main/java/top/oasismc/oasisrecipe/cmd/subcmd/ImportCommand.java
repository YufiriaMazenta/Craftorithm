package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.ItemLoader;
import top.oasismc.oasisrecipe.item.nbt.NBTManager;

import java.util.List;

public final class ImportCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ImportCommand();

    private ImportCommand() {
        super("import", null);
        regSubCommand(new AbstractSubCommand("results", null) {
            @Override
            public boolean onCommand(CommandSender sender, List<String> args) {
                if (args.size() < 1) {
                    OasisRecipe.getInstance().sendMsg(sender, "commands.missingParam");
                    return true;
                }
                ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                ConfigFile configFile = ItemLoader.getResultFile();
                NBTManager.importItem(args.get(0), item, configFile);
                OasisRecipe.getInstance().sendMsg(sender, "commands.import");
                return true;
            }
        });
        regSubCommand(new AbstractSubCommand("items", null) {
            @Override
            public boolean onCommand(CommandSender sender, List<String> args) {
                if (args.size() < 1) {
                    OasisRecipe.getInstance().sendMsg(sender, "commands.missingParam");
                    return true;
                }
                ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                ConfigFile configFile = ItemLoader.getItemFile();
                NBTManager.importItem(args.get(0), item, configFile);
                OasisRecipe.getInstance().sendMsg(sender, "commands.import");
                return true;
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)){
            OasisRecipe.getInstance().sendMsg(sender, "commands.playerOnly");
            return true;
        }
        return super.onCommand(sender, args);
    }
}
