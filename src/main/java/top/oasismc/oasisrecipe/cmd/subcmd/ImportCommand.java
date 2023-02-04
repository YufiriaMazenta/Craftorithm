package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.config.YamlFileWrapper;
import top.oasismc.oasisrecipe.util.ItemUtil;
import top.oasismc.oasisrecipe.util.MsgUtil;

import java.util.List;

public final class ImportCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ImportCommand();

    private ImportCommand() {
        super("import", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            MsgUtil.sendMsg(sender, "commands.missingParam");
            return true;
        }
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        YamlFileWrapper configFile = new YamlFileWrapper("items.yml");
        ItemUtil.saveItem2Config(item, configFile, args.get(0));
        MsgUtil.sendMsg(sender, "commands.import");
        return true;
    }
}
