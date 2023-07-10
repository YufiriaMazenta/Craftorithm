package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtil;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.lib.command.ISubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", "craftorithm.command.reload");
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
        RemoveRecipeCommand.getRemovedRecipeConfig().reloadConfig();
        ItemManager.loadItemFiles();
        RecipeManager.loadRecipeFiles();
        ArcencielDispatcher.INSTANCE.getFunctionFile().reloadConfig();
        ItemUtil.reloadCannotCraftLore();
    }

    public static void reloadRecipes() {
        RecipeManager.loadRecipes();
    }

}
