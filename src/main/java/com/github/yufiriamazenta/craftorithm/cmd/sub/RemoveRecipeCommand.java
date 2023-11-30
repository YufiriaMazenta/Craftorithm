package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubcmdExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * 重构为只能删除本身插件的配方，取消其他插件配方移动到disable命令
 */
public final class RemoveRecipeCommand extends AbstractSubCommand {

    public static final ISubcmdExecutor INSTANCE = new RemoveRecipeCommand();

    private RemoveRecipeCommand() {
        super("remove", "craftorithm.command.remove");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }

        if (RecipeManager.removeCraftorithmRecipe(args.get(0), true)) {
            LangUtil.sendLang(sender, Languages.commandRemoveSuccess.value());
        }
        else
            LangUtil.sendLang(sender, Languages.commandRemoveNotExist.value());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (String key : RecipeManager.recipeGroupMap().keySet()) {
                if (key.startsWith(args.get(0)))
                    tabList.add(key);
            }
            for (String key : RecipeManager.potionMixGroupMap().keySet()) {
                if (key.startsWith(args.get(0)))
                    tabList.add(key);
            }
            filterTabList(tabList, args.get(0));
            return tabList;
        }
        return super.onTabComplete(sender, args);
    }



}
