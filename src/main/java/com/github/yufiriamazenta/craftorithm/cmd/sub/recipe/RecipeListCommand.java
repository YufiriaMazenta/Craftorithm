package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenu;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeListMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.command.SubcommandHandler;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class RecipeListCommand extends AbstractSubCommand {

    public static final RecipeListCommand INSTANCE = new RecipeListCommand();
    private static final String CRAFTORITHM = "craftorithm", SERVER = "server";

    private RecipeListCommand() {
        super("list", "craftorithm.command.list");
    }

    @Subcommand
    SubcommandHandler server = new SubcommandHandler(SERVER, new PermInfo("craftorithm.command.list.server")) {
        @Override
        public boolean execute(CommandSender sender, List<String> args) {
            new RecipeListMenu((Player) sender, RecipeManager.INSTANCE.serverRecipesCache().keySet()).openMenu();
            return true;
        }
    };

    @Subcommand
    SubcommandHandler craftorithm = new SubcommandHandler(CRAFTORITHM, new PermInfo("craftorithm.command.list")) {
        @Override
        public boolean execute(CommandSender sender, List<String> args) {
            new RecipeGroupListMenu((Player) sender).openMenu();
            return true;
        }
    };

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender)) {
            return true;
        }
        return craftorithm.execute(sender, args);
    }

}
