package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayList;
import java.util.List;

public final class DisableRecipeCommand extends CommandNode {

    public static final DisableRecipeCommand INSTANCE = new DisableRecipeCommand();

    private DisableRecipeCommand() {
        super(CommandInfo
            .builder("disable")
            .permission(new PermInfo("craftorithm.command.disable"))
            .usage("&r/craftorithm disable <recipe_id>")
            .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        NamespacedKey disableRecipeKey = NamespacedKey.fromString(args.get(0));
        if (!RecipeManager.INSTANCE.serverRecipesCache().containsKey(disableRecipeKey)) {
            LangUtils.sendLang(invoker, Languages.COMMAND_DISABLE_NOT_EXIST);
            return;
        }
        if (RecipeManager.INSTANCE.disableRecipe(disableRecipeKey, true)) {
            LangUtils.sendLang(invoker, Languages.COMMAND_DISABLE_SUCCESS);
        }
        else
            LangUtils.sendLang(invoker, Languages.COMMAND_DISABLE_FAILED);
    }

    @Override
    public List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (NamespacedKey key : RecipeManager.INSTANCE.serverRecipesCache().keySet()) {
                String str = key.toString();
                if (str.contains(args.get(0)))
                    tabList.add(key.toString());
            }
            return tabList;
        }
        return List.of("");
    }
}
