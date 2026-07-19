package pers.yufiria.craftorithm.command.recipe;

import crypticlib.Invoker;
import crypticlib.command.CommandInfo;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.Bukkit;
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

import java.util.*;
import java.util.stream.Collectors;

public class RecipeBookCommand extends CommandNode {

    public static final RecipeBookCommand INSTANCE = new RecipeBookCommand();

    private static final String FLAG_PLAYER = "--player";
    private static final String FLAG_TYPE = "--type";

    private RecipeBookCommand() {
        super(
            CommandInfo
                .builder("recipebook")
                .permission(new PermInfo("craftorithm.command.recipebook"))
                .usage("&r/craftorithm recipebook [--player <name>] [--type <type>]")
                .build()
        );
    }

    @Override
    public void execute(@NotNull Invoker invoker, List<String> args) {
        // Resolve target player
        Player target;
        String playerName = CommandUtils.parseFlag(args, FLAG_PLAYER);
        if (playerName != null) {
            target = Bukkit.getPlayerExact(playerName);
            if (target == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_UNKNOWN_PLAYER, Map.of("<player_name>", playerName));
                return;
            }
        } else {
            if (!CommandUtils.checkInvokerIsPlayer(invoker)) {
                return;
            }
            Optional<Player> targetOpt = invoker.asPlayer().getPlatformPlayer(Bukkit::getPlayer);
            if (targetOpt.isEmpty()) {
                LangUtils.sendLang(invoker, Languages.COMMAND_PLAYER_ONLY);
                return;
            }
            target = targetOpt.get();
        }

        String typeKey = CommandUtils.parseFlag(args, FLAG_TYPE);
        if (typeKey != null) {
            if (typeKey.equalsIgnoreCase("all")) {
                // type为null时显示所有配方
                new RecipeListMenu(target, null, SortMode.NAME_ASC).openMenu();
                return;
            }
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(typeKey);
            if (recipeType == null) {
                LangUtils.sendLang(invoker, Languages.COMMAND_RECIPEBOOK_TYPE_NOT_FOUND, Map.of("<recipe_type>", typeKey));
                return;
            }
            new RecipeListMenu(target, recipeType, SortMode.NAME_ASC).openMenu();
        } else {
            new RecipeBookTypeSelectMenu(target).openMenu();
        }
    }

    @Override
    public void onNoPerm(@NotNull Invoker invoker, @NotNull List<String> args) {
        LangUtils.sendLang(invoker, Languages.COMMAND_NO_PERM);
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull Invoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return Arrays.asList(FLAG_PLAYER, FLAG_TYPE);
        }

        String lastArg = args.get(args.size() - 2);
        switch (lastArg.toLowerCase()) {
            case FLAG_PLAYER:
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            case FLAG_TYPE:
                List<String> typeSuggestions = RecipeManager.INSTANCE.getRecipeTypes().stream()
                    .filter(type -> type.typeId() != 0) // 过滤掉UNKNOWN类型
                    .map(RecipeType::typeKey)
                    .collect(Collectors.toList());
                typeSuggestions.add("all");
                return typeSuggestions;
            default:
                List<String> suggestions = new ArrayList<>();
                if (!CommandUtils.hasFlag(args, FLAG_PLAYER)) {
                    suggestions.add(FLAG_PLAYER);
                }
                if (!CommandUtils.hasFlag(args, FLAG_TYPE)) {
                    suggestions.add(FLAG_TYPE);
                }
                return suggestions;
        }
    }

}