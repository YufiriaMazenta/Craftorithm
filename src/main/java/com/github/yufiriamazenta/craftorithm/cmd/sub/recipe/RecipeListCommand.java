package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenu;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeListMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class RecipeListCommand extends BukkitSubcommand {

    public static final RecipeListCommand INSTANCE = new RecipeListCommand();

    private RecipeListCommand() {
        super(CommandInfo.builder("craftorithm.command.list").permission(new PermInfo("craftorithm.command.list")).build());
    }

    @Subcommand
    BukkitSubcommand server = new BukkitSubcommand("server", new PermInfo("craftorithm.command.list.server")) {
        @Override
        public void execute(CommandSender sender, List<String> args) {
            if (!CommandUtils.checkSenderIsPlayer(sender)) {
                return;
            }
            new RecipeListMenu((Player) sender, RecipeManager.INSTANCE.serverRecipesCache().keySet()).openMenu();
        }
    };

    @Subcommand
    BukkitSubcommand craftorithm = new BukkitSubcommand("craftorithm", new PermInfo("craftorithm.command.list")) {
        @Override
        public void execute(CommandSender sender, List<String> args) {
            if (!CommandUtils.checkSenderIsPlayer(sender)) {
                return;
            }
            new RecipeGroupListMenu((Player) sender).openMenu();
        }
    };

    @Override
    public void execute(CommandSender sender, List<String> args) {
        craftorithm.execute(sender, args);
    }

}
