package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.menu.impl.recipe.RecipeListMenuHolder;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

//TODO 展示全服所有配方
public final class RecipeListCommand extends AbstractSubCommand {

    public static final RecipeListCommand INSTANCE = new RecipeListCommand();

    private RecipeListCommand() {
        super("list", "craftorithm.command.list");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender)) {
            LangUtil.sendLang(sender, "command.player_only");
            return true;
        }
        Player player = (Player) sender;
        player.openInventory(new RecipeListMenuHolder().getInventory());
        return true;
    }

}
