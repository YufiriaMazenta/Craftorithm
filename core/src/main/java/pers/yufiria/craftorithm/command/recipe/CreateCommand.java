package pers.yufiria.craftorithm.command.recipe;

import crypticlib.Invoker;
import crypticlib.Key;
import crypticlib.command.CommandContext;
import pers.yufiria.craftorithm.command.AbstractSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import crypticlib.ui.menu.Menu;
import crypticlib.util.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.permission.Permissions;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.creator.RecipeCreatorManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CreateCommand extends AbstractSubcommand {

    public static final CreateCommand INSTANCE = new CreateCommand();

    private CreateCommand() {
        super(CommandInfo
            .builder("create")
            .permission(new PermInfo(Permissions.RECIPE_CREATE))
            .usage("&r/craftorithm create <recipe_type> [recipe_id] [recipe_file_name]")
            .build()
        );
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        Invoker invoker = context.invoker();
        List<String> args = context.args();
        if (!CommandUtils.checkInvokerIsPlayer(invoker))
            return;
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        String recipeTypeStr = args.getFirst();
        String recipeId = null;
        String recipeFileName = null;

        if (args.size() >= 2) {
            recipeId = args.get(1);
            if (!Key.isValidKey(recipeId)) {
                LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
                return;
            }
            if (RecipeManager.INSTANCE.containsRecipe(recipeId)) {
                LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_NAME_USED);
                return;
            }
        }
        if (args.size() >= 3) {
            recipeFileName = args.get(2);
        }

        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeTypeByKey(recipeTypeStr);
        if (recipeType == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        Optional<TriFunction<Player, String, String, Menu>> creatorFactoryOpt = RecipeCreatorManager.INSTANCE.getCreatorFactory(recipeType);
        if (creatorFactoryOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        Optional<Player> playerOpt = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer);
        if (playerOpt.isEmpty()) {
            LangUtils.sendLang(invoker, Languages.COMMAND_PLAYER_ONLY);
            return;
        }
        creatorFactoryOpt.get().apply(playerOpt.get(), recipeId, recipeFileName);
    }


    @Override
    public List<String> tabComplete(@NotNull CommandContext context) {
        List<String> args = context.args();
        if (args.size() <= 1) {
            return RecipeCreatorManager.INSTANCE.recipeTypes().stream().map(RecipeType::typeKey).toList();
        }
        if (args.size() == 2) {
            return Collections.singletonList("<recipe_id>");
        }
        return Collections.singletonList("<recipe_file_name>");
    }

}
