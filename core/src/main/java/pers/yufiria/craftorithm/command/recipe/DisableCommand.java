package pers.yufiria.craftorithm.command.recipe;

import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayList;
import java.util.List;

public final class DisableCommand extends CommandNode {

    public static final DisableCommand INSTANCE = new DisableCommand();

    private DisableCommand() {
        super(CommandInfo
            .builder("disable")
            .permission(new PermInfo("craftorithm.command.disable"))
            .usage("&r/craftorithm disable <recipe_id>")
            .build()
        );
    }

    @Override
    public void execute(@NotNull Invoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        NamespacedKey recipeKey = NamespacedKey.fromString(args.get(0));
        if (!RecipeManager.INSTANCE.serverRecipeKeys().contains(recipeKey)) {
            LangUtils.sendLang(invoker, Languages.COMMAND_DISABLE_NOT_EXIST);
            return;
        }
        if (RecipeManager.INSTANCE.disableRecipe(recipeKey, true)) {
            LangUtils.sendLang(invoker, Languages.COMMAND_DISABLE_SUCCESS);
        } else
            LangUtils.sendLang(invoker, Languages.COMMAND_DISABLE_FAILED);
    }

    @Override
    public void onNoPerm(@NotNull Invoker invoker, @NotNull List<String> args) {
        LangUtils.sendLang(invoker, Languages.COMMAND_NO_PERM);
    }

    @Override
    public List<String> tabComplete(@NotNull Invoker invoker, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (NamespacedKey recipeKey : RecipeManager.INSTANCE.serverRecipeKeys()) {
                String str = recipeKey.toString();
                if (str.contains(args.get(0)))
                    tabList.add(recipeKey.toString());
            }
            return tabList;
        }
        return List.of("");
    }
}
