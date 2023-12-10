package com.github.yufiriamazenta.craftorithm.cmd.sub;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenuHolder;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeListMenuHolder;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class RecipeListCommand extends AbstractSubCommand {

    public static final RecipeListCommand INSTANCE = new RecipeListCommand();
    private static final String CRAFTORITHM = "craftorithm", SERVER = "server";

    private RecipeListCommand() {
        super("list", "craftorithm.command.list");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (CrypticLib.minecraftVersion() < 11600) {
            LangUtil.sendLang(sender, Languages.COMMAND_LIST_UNSUPPORTED_VERSION.value());
            return true;
        }
        if (!checkSenderIsPlayer(sender)) {
            return true;
        }
        String listType;
        if (args.isEmpty())
            listType = CRAFTORITHM;
        else
            listType = args.get(0).toLowerCase(Locale.ENGLISH);
        Player player = (Player) sender;
        switch (listType) {
            case SERVER:
                player.openInventory(new RecipeListMenuHolder(player, RecipeManager.INSTANCE.serverRecipesCache().keySet()).getInventory());
                break;
            case CRAFTORITHM:
            default:
                player.openInventory(new RecipeGroupListMenuHolder().getInventory());
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> list = new ArrayList<>(Arrays.asList(CRAFTORITHM, SERVER));
            filterTabList(list, args.get(0));
            return list;
        }
        return super.onTabComplete(sender, args);
    }
}
