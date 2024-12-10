package pers.yufiria.craftorithm.cmd.sub.recipe;

import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.CommandUtils;
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
        super(CommandInfo.builder("list").permission(new PermInfo("craftorithm.command.list")).build());
    }

    @Subcommand
    BukkitSubcommand server = new BukkitSubcommand("server", new PermInfo("craftorithm.command.list.server")) {
        @Override
        public void execute(CommandSender sender, List<String> args) {
            if (!CommandUtils.checkSenderIsPlayer(sender)) {
                return;
            }
            //TODO
//            new RecipeListMenu((Player) sender, RecipeManager.INSTANCE.serverRecipesCache().keySet()).openMenu();
        }
    };

    @Subcommand
    BukkitSubcommand craftorithm = new BukkitSubcommand("craftorithm", new PermInfo("craftorithm.command.list")) {
        @Override
        public void execute(CommandSender sender, List<String> args) {
            if (!CommandUtils.checkSenderIsPlayer(sender)) {
                return;
            }
            //todo
//            new RecipeGroupListMenu((Player) sender).openMenu();
        }
    };

    @Override
    public void execute(CommandSender sender, List<String> args) {
        craftorithm.execute(sender, args);
    }

}
