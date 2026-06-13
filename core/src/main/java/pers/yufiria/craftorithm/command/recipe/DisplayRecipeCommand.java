package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import crypticlib.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.BiFunction;

public class DisplayRecipeCommand extends CommandNode {

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
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        Player target;
        if (args.size() >= 2) {
            String targetName = args.get(1);
            Player player = Bukkit.getPlayer(targetName);
            if (player == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_UNKNOWN_PLAYER, Map.of("<player_name>", targetName));
                return;
            }
            target = player;
        } else {
            if (!CommandUtils.checkInvokerIsPlayer(invoker)) {
                return;
            }
            target = (Player) invoker.asPlayer().getPlatformPlayer();
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(args.get(0));
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(namespacedKey);
        if (recipe == null) {
            return;
        }
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        Optional<BiFunction<Player, Recipe, Menu>> recipeDisplayOpt = RecipeDisplayManager.INSTANCE.getRecipeDisplay(recipeType);
        recipeDisplayOpt.ifPresentOrElse(displayFunc -> {
            displayFunc.apply(target, recipe);
        }, () -> {
            LangUtils.sendLang(target, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
        });
    }

    @Override
    public @Nullable List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            Set<NamespacedKey> recipes = new LinkedHashSet<>(RecipeManager.INSTANCE.craftorithmRecipes().keySet());
            recipes.addAll(RecipeManager.INSTANCE.craftorithmRecipes().keySet());
            return recipes.stream().map(NamespacedKey::toString).toList();
        } else if (args.size() == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return Collections.singletonList("");
    }



}
