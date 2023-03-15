package com.github.yufiriamazenta.craftorithm.cmd.subcmd;

import com.github.yufiriamazenta.craftorithm.menu.impl.recipe.RecipeCreatorMenuHolder;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CreateCommand extends AbstractSubCommand {

    public static final CreateCommand INSTANCE = new CreateCommand();
    private final List<String> recipeTypeList;

    protected CreateCommand() {
        super("create", "craftorithm.command.create");
        recipeTypeList = Arrays.stream(RecipeType.values()).map(RecipeType::name).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender))
            return true;
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        RecipeType recipeType = RecipeType.valueOf(args.get(0).toUpperCase(Locale.ROOT));
        ((Player) sender).openInventory(new RecipeCreatorMenuHolder(recipeType, args.get(1)).getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>(recipeTypeList);
            filterTabList(tabList, args.get(0));
            return tabList;
        }
        return Collections.singletonList("<recipe_name>");
    }

}
