package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.recipeBook.RecipeBookTypeSelectMenu;
import pers.yufiria.craftorithm.ui.recipeBook.RecipeListMenu;
import pers.yufiria.craftorithm.ui.recipeBook.SortMode;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeBookCommand extends CommandNode {

    public static final RecipeBookCommand INSTANCE = new RecipeBookCommand();

    private RecipeBookCommand() {
        super(
            CommandInfo
                .builder("recipebook")
                .permission(new PermInfo("craftorithm.command.recipebook"))
                .usage("&r/craftorithm recipebook [recipe_type]")
                .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        if (!CommandUtils.checkInvokerIsPlayer(invoker)) {
            return;
        }
        Player target = (Player) invoker.asPlayer().getPlatformPlayer();

        if (!args.isEmpty()) {
            String recipeTypeKey = args.get(0);
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipeTypeKey);

            if (recipeType == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_RECIPEBOOK_TYPE_NOT_FOUND, Map.of("<recipe_type>", recipeTypeKey));
                return;
            }

            RecipeListMenu listMenu = new RecipeListMenu(target, recipeType, SortMode.NAME_ASC);
            listMenu.openMenu();
        } else {
            new RecipeBookTypeSelectMenu(target).openMenu();
        }
    }

    @Override
    public @Nullable List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return RecipeManager.INSTANCE.getRecipeTypes().stream()
                .map(RecipeType::typeKey)
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}