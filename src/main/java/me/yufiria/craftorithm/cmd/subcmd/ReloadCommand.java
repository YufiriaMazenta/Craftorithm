package me.yufiria.craftorithm.cmd.subcmd;

import me.yufiria.craftorithm.Craftorithm;
import me.yufiria.craftorithm.api.cmd.AbstractSubCommand;
import me.yufiria.craftorithm.api.cmd.ISubCommand;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import me.yufiria.craftorithm.item.ItemManager;
import me.yufiria.craftorithm.recipe.RecipeManager;
import me.yufiria.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        try {
            reloadPlugin();
            LangUtil.sendMsg(sender, "command.reload.success");
        } catch (Exception e) {
            e.printStackTrace();
            LangUtil.sendMsg(sender, "command.reload.exception");
        }
        return true;
    }

    public static void reloadPlugin() {
        reloadConfigs();
        ItemManager.loadItems();
        reloadRecipes();
    }

    public static void reloadConfigs() {
        Craftorithm.getInstance().reloadConfig();
        LangUtil.reloadMsgConfig();
        RemoveCommand.getRemovedRecipeConfig().reloadConfig();
        ItemManager.loadItemFiles();
        RecipeManager.loadRecipeFiles();
        ArcencielDispatcher.INSTANCE.getFunctionFile().reloadConfig();
    }

    public static void reloadRecipes() {
        RecipeManager.reloadRecipes();
    }

}
