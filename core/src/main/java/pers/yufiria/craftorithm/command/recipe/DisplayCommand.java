package pers.yufiria.craftorithm.command.recipe;

import crypticlib.Invoker;
import crypticlib.command.CommandContext;
import pers.yufiria.craftorithm.command.AbstractSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import crypticlib.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.permission.Permissions;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.util.*;
import java.util.function.BiFunction;

public final class DisplayCommand extends AbstractSubcommand {

    public static final DisplayCommand INSTANCE = new DisplayCommand();

    private DisplayCommand() {
        super(
            CommandInfo
                .builder("display")
                .permission(new PermInfo(Permissions.RECIPE_DISPLAY))
                .usage("&r/craftorithm display <recipe_id>")
                .build()
        );
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        Invoker invoker = context.invoker();
        List<String> args = context.args();
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        Player target;
        if (args.size() >= 2) {
            String targetName = args.get(1);
            target = Bukkit.getPlayer(targetName);
            if (target == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_UNKNOWN_PLAYER, Map.of("<player_name>", targetName));
                return;
            }
        } else {
            target = PlayerUtils.resolveInvokerPlayer(invoker);
            if (target == null) {
                return;
            }
        }
        NamespacedKey recipeKey = NamespacedKey.fromString(args.get(0));
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(recipeKey);
        if (recipe == null) {
            return;
        }
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        Optional<BiFunction<Player, Recipe, Menu>> displayFactoryOpt = RecipeDisplayManager.INSTANCE.getRecipeDisplayFactory(recipeType);
        displayFactoryOpt.ifPresentOrElse(displayFactory -> {
            displayFactory.apply(target, recipe);
        }, () -> {
            LangUtils.sendLang(target, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
        });
    }


    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandContext context) {
        List<String> args = context.args();
        if (args.size() <= 1) {
            Set<NamespacedKey> recipes = new LinkedHashSet<>(RecipeManager.INSTANCE.craftorithmRecipes().keySet());
            recipes.addAll(RecipeManager.INSTANCE.serverRecipeKeys());
            return recipes.stream().map(NamespacedKey::toString).toList();
        } else if (args.size() == 2) {
            return PlayerUtils.onlinePlayerNames();
        }
        return Collections.singletonList("");
    }

}
