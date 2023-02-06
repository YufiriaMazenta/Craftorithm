package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.item.ItemManager;
import top.oasismc.oasisrecipe.recipe.handler.OldRecipeManager;
import top.oasismc.oasisrecipe.util.MsgUtil;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            reloadPlugin();
            MsgUtil.sendMsg(sender, "command.reload");
            return true;
        }
        return super.onCommand(sender, args);
    }

    public static void reloadPlugin() {
        reloadConfigs();
        ItemManager.loadItems();
        reloadRecipes();
    }

    public static void reloadConfigs() {
        OasisRecipe.getInstance().reloadConfig();
        MsgUtil.reloadMsgConfig();
        OldRecipeManager.INSTANCE.getRecipeFile().reloadConfig();
        RemoveCommand.getRemovedRecipeConfig().reloadConfig();
        ItemManager.loadItemFiles();
    }

    public static void reloadRecipes() {
        OldRecipeManager.INSTANCE.reloadRecipes();
    }

}
