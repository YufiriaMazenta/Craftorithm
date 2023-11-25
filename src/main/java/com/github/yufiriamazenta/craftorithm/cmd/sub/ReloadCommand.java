package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubcmdExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final ISubcmdExecutor INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", "craftorithm.command.reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        try {
            reloadPlugin();
            LangUtil.sendLang(sender, "command.reload.success");
        } catch (Exception e) {
            e.printStackTrace();
            LangUtil.sendLang(sender, "command.reload.exception");
        }
        return true;
    }

    public static void reloadPlugin() {
        reloadConfigs();
        ItemManager.reloadItemManager();
        RecipeManager.reloadRecipeManager();
    }

    public static void reloadConfigs() {
        Craftorithm.instance().reloadConfig();
        ItemUtils.reloadCannotCraftLore();
        LangUtil.reloadLangConfig();
        ArcencielDispatcher.INSTANCE.functionFile().reloadConfig();
    }

}
