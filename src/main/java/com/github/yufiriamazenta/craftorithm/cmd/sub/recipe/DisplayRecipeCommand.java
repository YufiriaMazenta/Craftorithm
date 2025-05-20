package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.menu.display.RecipeDisplayMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayRecipeCommand extends BukkitSubcommand {

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
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        Player target;
        if (args.size() >= 2) {
            String targetName = args.get(1);
            Player player = Bukkit.getPlayer(targetName);
            if (player == null) {
                //TODO 提示消息
                IOHelper.info("&cUnknown player: " + targetName);
                return;
            }
            target = player;
        } else {
            if (!CommandUtils.checkSenderIsPlayer(sender))
                return;
            target = (Player) sender;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(args.get(0));
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(namespacedKey);
        if (recipe == null) {
            return;
        }
        new RecipeDisplayMenu(target, recipe, null).openMenu();
    }

    @Override
    public @Nullable List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>();
            for (NamespacedKey key : RecipeManager.INSTANCE.serverRecipesCache().keySet()) {
                String str = key.toString();
                if (str.contains(args.get(0)))
                    tabList.add(key.toString());
            }
            return tabList;
        } else if (args.size() == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return Collections.singletonList("");
    }
}
