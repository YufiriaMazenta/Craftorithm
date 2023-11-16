package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.command.ISubCmdExecutor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class RemoveRecipeCommand extends AbstractSubCommand {

    public static final ISubCmdExecutor INSTANCE = new RemoveRecipeCommand();

    private RemoveRecipeCommand() {
        super("remove", "craftorithm.command.remove");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        if (RecipeManager.removeRecipe(args.get(0), true)) {
            LangUtil.sendLang(sender, "command.remove.success");
        }
        else
            LangUtil.sendLang(sender, "command.remove.not_exist");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (NamespacedKey key : RecipeManager.getServerRecipeMap().keySet()) {
                String str = key.toString();
                if (str.startsWith(args.get(0)))
                    tabList.add(key.toString());
            }
            filterTabList(tabList, args.get(0));
            return tabList;
        }
        return super.onTabComplete(sender, args);
    }



}
