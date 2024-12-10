package pers.yufiria.craftorithm.cmd.sub.recipe;

import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayRecipeCommand extends BukkitSubcommand {

    public static final DisplayRecipeCommand INSTANCE = new DisplayRecipeCommand();

    protected DisplayRecipeCommand() {
        super(
            CommandInfo
                .builder("display")
                .permission(new PermInfo("craftorithm.command.display"))
                .usage("&r/craftorithm display <recipe_id>")
                .build()
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!CommandUtils.checkSenderIsPlayer(sender))
            return;
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(args.get(0));
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(namespacedKey);
        if (recipe == null) {
            return;
        }
        //TODO 展示
//        new RecipeDisplayMenu((Player) sender, recipe, null).openMenu();
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
