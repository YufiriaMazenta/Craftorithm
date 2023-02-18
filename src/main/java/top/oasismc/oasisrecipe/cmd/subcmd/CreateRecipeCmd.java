package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.menu.CreateRecipeMenu;
import top.oasismc.oasisrecipe.recipe.RecipeManager;
import top.oasismc.oasisrecipe.util.LangUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateRecipeCmd extends AbstractSubCommand {

    public static final CreateRecipeCmd INSTANCE = new CreateRecipeCmd();
    private final List<String> recipeTypeList = new ArrayList<>(RecipeManager.recipeBuilderMap.keySet());

    private CreateRecipeCmd() {
        super("create");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        if (!(sender instanceof Player)) {
            LangUtil.sendMsg(sender, "command.player_only");
            return true;
        }
        if (!recipeTypeList.contains(args.get(0))) {
            LangUtil.sendMsg(sender, "command.create.not_exist_type");
            return true;
        }
        ((Player) sender).openInventory(CreateRecipeMenu.buildMenu(args.get(0), args.get(1)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> list = new ArrayList<>(recipeTypeList);
            list.removeIf(str -> str.startsWith(args.get(0)));
            return list;
        } else {
            return Collections.singletonList("");
        }
    }
}
