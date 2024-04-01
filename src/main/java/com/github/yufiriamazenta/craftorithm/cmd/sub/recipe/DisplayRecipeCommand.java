package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeDisplayMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayRecipeCommand extends AbstractSubCommand {

    public static final DisplayRecipeCommand INSTANCE = new DisplayRecipeCommand();

    protected DisplayRecipeCommand() {
        super("display", "craftorithm.command.display");
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender))
            return true;
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 1);
            return true;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(args.get(0));
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(namespacedKey);
        if (recipe == null) {
            return true;
        }
        new RecipeDisplayMenu((Player) sender, recipe, null).openMenu();
        return true;
    }

    @Override
    public @Nullable List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (NamespacedKey key : RecipeManager.INSTANCE.serverRecipesCache().keySet()) {
                String str = key.toString();
                if (str.contains(args.get(0)))
                    tabList.add(key.toString());
            }
            return tabList;
        }
        return Collections.singletonList("");
    }
}
