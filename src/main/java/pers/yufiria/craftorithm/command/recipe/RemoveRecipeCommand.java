package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Collections;
import java.util.List;

/**
 * 重构为只能删除本身插件的配方，取消其他插件配方移动到disable命令
 */
public final class RemoveRecipeCommand extends CommandNode {

    public static final RemoveRecipeCommand INSTANCE = new RemoveRecipeCommand();

    private RemoveRecipeCommand() {
        super(CommandInfo
            .builder("remove")
            .permission(new PermInfo("craftorithm.recipe.remove"))
            .usage("&r/craftorithm remove <recipe_name>")
            .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }

        NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), args.get(0));
        if (RecipeManager.INSTANCE.removeCraftorithmRecipe(recipeKey, true, true)) {
            LangUtils.sendLang(invoker, Languages.COMMAND_REMOVE_SUCCESS);
        }
        else
            LangUtils.sendLang(invoker, Languages.COMMAND_REMOVE_NOT_EXIST);
    }

    @Override
    public List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return RecipeManager.INSTANCE.craftorithmRecipes().keySet().stream().map(NamespacedKey::getKey).toList();
        }
        return Collections.emptyList();
    }



}
