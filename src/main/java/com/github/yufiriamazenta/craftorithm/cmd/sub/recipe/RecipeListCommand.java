package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenu;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeListMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.command.SubcommandHandler;
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
        regSub(new SubcommandHandler(SERVER) {
            @Override
            public boolean execute(CommandSender sender, List<String> args) {
                new RecipeListMenu((Player) sender, RecipeManager.INSTANCE.serverRecipesCache().keySet()).openMenu();
                return true;
            }
        }.setPermission(new PermInfo("craftorithm.command.list.server")));
        regSub(
            new SubcommandHandler(CRAFTORITHM) {
                @Override
                public boolean execute(CommandSender sender, List<String> args) {
                    new RecipeGroupListMenu((Player) sender).openMenu();
                    return true;
                }
            }.setPermission("craftorithm.command.list"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender)) {
            return true;
        }
        if (args.isEmpty()) {
            args = new ArrayList<>(Collections.singletonList(CRAFTORITHM));
        }
        return super.onCommand(sender, args);
    }

}
