package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


public final class RecipeListCommand extends AbstractSubCommand {

    public static final RecipeListCommand INSTANCE = new RecipeListCommand();

    private RecipeListCommand() {
        super("list", "craftorithm.command.list");
    }

    @Subcommand
    BukkitSubcommand server = new BukkitSubcommand("server", new PermInfo("craftorithm.command.list.server")) {
        @Override
        public void execute(CommandSender sender, List<String> args) {
            if (!checkSenderIsPlayer(sender)) {
                return;
            }
            //TODO 页面重写
//            new RecipeListMenu((Player) sender, RecipeManager.INSTANCE.serverRecipesCache().keySet()).openMenu();
        }
    };

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender)) {
            return;
        }
        //TODO 页面重写
//        new RecipeGroupListMenu((Player) sender).openMenu();
    }

}
