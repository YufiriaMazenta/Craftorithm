package pers.yufiria.craftorithm.command.recipe;

import crypticlib.Invoker;
import crypticlib.Key;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.List;
import java.util.Map;

/**
 * 恢复一个被禁用的配方
 */
public class RestoreCommand extends CommandNode {

    public static final RestoreCommand INSTANCE = new RestoreCommand();

    private RestoreCommand() {
        super(CommandInfo
            .builder("restore")
            .permission(new PermInfo("craftorithm.command.restore"))
            .usage("&r/craftorithm restore <recipe_id>")
            .build()
        );
    }

    @Override
    public void execute(@NotNull Invoker invoker, @NotNull List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        Key key = Key.key(args.getFirst());
        if (key == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_RESTORE_INVALID_RECIPE_ID);
            return;
        }
        NamespacedKey recipeKey = new NamespacedKey(key.namespace(), key.key());
        boolean result = RecipeManager.INSTANCE.restoreDisabledRecipe(recipeKey);
        if (result) {
            LangUtils.sendLang(invoker, Languages.COMMAND_RESTORE_SUCCESS, Map.of("<recipe_id>", recipeKey.asString()));
        } else {
            LangUtils.sendLang(invoker, Languages.COMMAND_RESTORE_FAILED, Map.of("<recipe_id>", recipeKey.asString()));
        }
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull Invoker invoker, @NotNull List<String> args) {
        return RecipeManager.INSTANCE.disableRecipeKeys().stream().map(NamespacedKey::asString).toList();
    }
}
