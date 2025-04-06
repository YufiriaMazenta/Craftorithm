package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.util.CommandUtils;

import java.util.List;

public final class RecipeListCommand extends CommandNode {

    public static final RecipeListCommand INSTANCE = new RecipeListCommand();

    private RecipeListCommand() {
        super(CommandInfo.builder("list").permission(new PermInfo("craftorithm.command.list")).build());
    }

    @Subcommand
    CommandNode server = new CommandNode("server", new PermInfo("craftorithm.command.list.server")) {
        @Override
        public void execute(@NotNull CommandInvoker invoker, List<String> args) {
            if (!CommandUtils.checkInvokerIsPlayer(invoker)) {
                return;
            }
            //TODO
//            new RecipeListMenu((Player) sender, RecipeManager.INSTANCE.serverRecipesCache().keySet()).openMenu();
        }
    };

    @Subcommand
    CommandNode craftorithm = new CommandNode("craftorithm", new PermInfo("craftorithm.command.list")) {
        @Override
        public void execute(@NotNull CommandInvoker invoker, List<String> args) {
            if (!CommandUtils.checkInvokerIsPlayer(invoker)) {
                return;
            }
            //todo
//            new RecipeGroupListMenu((Player) sender).openMenu();
        }
    };

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        craftorithm.execute(invoker, args);
    }

}
