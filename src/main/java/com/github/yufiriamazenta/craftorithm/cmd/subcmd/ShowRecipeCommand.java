package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO 迁移命令到list
public final class ShowRecipeCommand extends AbstractSubCommand {

    public static final ShowRecipeCommand INSTANCE = new ShowRecipeCommand();

    private ShowRecipeCommand() {
        super("show", "craftorithm.command.show");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender)) {
            return true;
        }
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        List<Recipe> recipes = RecipeManager.getCraftorithmRecipe(args.get(0));
        if (recipes.isEmpty()) {
            LangUtil.sendLang(sender, "command.show.not_exist_recipe");
            return true;
        }
        Player player = (Player) sender;
        //TODO 完成新版配方展示
//        player.openInventory(new RecipeDisplayMenuHolder(recipe).getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        //TODO 修改返回列表
        if (args.size() <= 1) {
//            List<NamespacedKey> recipeKeyList = RecipeManager.getPluginRecipeKeys();
            List<String> tabList = new ArrayList<>();
//            for (NamespacedKey key : recipeKeyList) {
//                tabList.add(key.getKey());
//            }
//            filterTabList(tabList, args.get(0));
            return tabList;
        } else {
            return Collections.singletonList("");
        }
    }
}
