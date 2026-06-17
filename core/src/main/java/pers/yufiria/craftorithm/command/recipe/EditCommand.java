package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import crypticlib.ui.menu.Menu;
import crypticlib.util.TriFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.editor.RecipeEditorManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EditCommand extends CommandNode {

    public static final EditCommand INSTANCE = new EditCommand();

    private EditCommand() {
        super(
            CommandInfo
                .builder("edit")
                .permission(new PermInfo("craftorithm.edit_recipe"))
                .usage("&r/craftorithm editor <recipe_id>")
                .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, @NotNull List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        if (!CommandUtils.checkInvokerIsPlayer(invoker))
            return;

        String recipeIdStr = args.getFirst();
        NamespacedKey recipeKey = NamespacedKey.fromString(recipeIdStr);
        if (recipeKey == null) {
            recipeKey = NamespacedKey.fromString("craftorithm:" + recipeIdStr);
        }
        if (recipeKey == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_EDIT_INVALID_RECIPE_ID);
            return;
        }

        Recipe recipe = RecipeManager.INSTANCE.getRecipe(recipeKey);
        if (recipe == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_EDIT_RECIPE_NOT_FOUND);
            return;
        }

        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        Optional<TriFunction<Player, NamespacedKey, Recipe, Menu>> editorOpt = RecipeEditorManager.INSTANCE.getEditor(recipeType);
        if (editorOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
            return;
        }

        Player player = (Player) invoker.asPlayer().getPlatformPlayer();
        editorOpt.get().apply(player, recipeKey, recipe);
    }

    @Override
    public @NotNull List<String> tab(@NotNull CommandInvoker invoker, @NotNull List<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(RecipeManager.INSTANCE.craftorithmRecipes().keySet().stream()
                .map(NamespacedKey::toString)
                .toList());
        }
        return List.of("");
    }

}
