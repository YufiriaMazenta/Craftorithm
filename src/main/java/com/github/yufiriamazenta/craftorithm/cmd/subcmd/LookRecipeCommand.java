package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.menu.impl.recipe.RecipeDisplayMenuHolder;
import com.github.yufiriamazenta.craftorithm.menu.impl.recipe.RecipeListMenuHolder;
import com.github.yufiriamazenta.craftorithm.recipe.DefRecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LookRecipeCommand extends AbstractSubCommand {

    public static final LookRecipeCommand INSTANCE = new LookRecipeCommand();

    protected LookRecipeCommand() {
        super("look", "craftorithm.command.look");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender)) {
            return true;
        }
        if (args.size() < 1) {
            Player player = (Player) sender;
            player.openInventory(new RecipeListMenuHolder().getInventory());
            return true;
        }
//        Recipe recipe = DefRecipeManager.getPluginRecipe(args.get(0));
//        if (recipe == null) {
//            LangUtil.sendMsg(sender, "command.look.not_exist_recipe");
//            return true;
//        }
//        Player player = (Player) sender;
//        player.openInventory(new RecipeDisplayMenuHolder(recipe).getInventory());
        //TODO
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
//            List<NamespacedKey> recipeKeyList = DefRecipeManager.getPluginRecipeKeys();
            List<String> tabList = new ArrayList<>();
//            for (NamespacedKey key : recipeKeyList) {
//                tabList.add(key.getKey());
//            }
            filterTabList(tabList, args.get(0));
            return tabList;
        } else {
            return Collections.singletonList("");
        }
    }
}
