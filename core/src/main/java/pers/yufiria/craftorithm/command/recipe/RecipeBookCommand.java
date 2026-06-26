package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.perm.PermInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.ui.recipeBook.RecipeBookTypeSelectMenu;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecipeBookCommand extends CommandNode {

    public static final RecipeBookCommand INSTANCE = new RecipeBookCommand();

    private RecipeBookCommand() {
        super(
            CommandInfo
                .builder("recipebook")
                .permission(new PermInfo("craftorithm.command.recipebook"))
                .usage("&r/craftorithm recipebook [player]")
                .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        Player target;
        if (!args.isEmpty()) {
            String targetName = args.get(0);
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
        new RecipeBookTypeSelectMenu(target).openMenu();
    }

    @Override
    public @Nullable List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return Collections.emptyList();
    }

}
