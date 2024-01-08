package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.menu.creator.*;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CreateRecipeCommand extends AbstractSubCommand {

    public static final CreateRecipeCommand INSTANCE = new CreateRecipeCommand();
    private final List<String> recipeTypeList;
    private final Pattern recipeNamePattern = Pattern.compile("^[a-z0-9._-]+$");

    private CreateRecipeCommand() {
        super("create", "craftorithm.command.create");
        recipeTypeList = Arrays.stream(RecipeType.values()).map(RecipeType::name).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        List<String> unsupportedRecipeTypeList = new ArrayList<>();
        unsupportedRecipeTypeList.add("random_cooking");
        unsupportedRecipeTypeList.add("unknown");
        if (CrypticLib.minecraftVersion() < 11400) {
            unsupportedRecipeTypeList.add("stone_cutting");
            unsupportedRecipeTypeList.add("smithing");
            unsupportedRecipeTypeList.add("cooking");
        }
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            unsupportedRecipeTypeList.add("potion");
        }
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            unsupportedRecipeTypeList.add("anvil");
        recipeTypeList.removeAll(unsupportedRecipeTypeList);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!checkSenderIsPlayer(sender))
            return true;
        if (args.isEmpty()) {
            sendNotEnoughCmdParamMsg(sender, 2);
            return true;
        }
        String recipeTypeStr = args.get(0).toLowerCase(Locale.ROOT);
        if (!recipeTypeList.contains(recipeTypeStr)) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return true;
        }
        String groupName = args.size() < 2 ? "global" : args.get(1);
        String recipeName = args.size() < 3 ? UUID.randomUUID().toString() : args.get(2);
        Matcher matcher = recipeNamePattern.matcher(groupName);
        if (!matcher.matches()) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
            return true;
        }
//        if (RecipeManager.INSTANCE.hasRecipeGroup(groupName)) {
//            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_NAME_USED);
//            return true;
//        }
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
        Player player = (Player) sender;
        switch (recipeType) {
            case SHAPED:
            case SHAPELESS:
                new CraftingRecipeCreator(player, recipeType, groupName, recipeName).openMenu();
                break;
            case COOKING:
                new CookingRecipeCreator(player, groupName, recipeName).openMenu();
                break;
            case SMITHING:
                new SmithingRecipeCreator(player, groupName, recipeName).openMenu();
                break;
            case STONE_CUTTING:
                new StoneCuttingRecipeCreator(player, groupName, recipeName).openMenu();
                break;
            case POTION:
                new PotionMixCreator(player, groupName, recipeName).openMenu();
                break;
            case ANVIL:
                new AnvilRecipeCreator(player, groupName, recipeName).openMenu();
                break;
            default:
                LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>(recipeTypeList);
            filterTabList(tabList, args.get(0));
            return tabList;
        } else if (args.size() == 2) {
            List<String> tabList = new ArrayList<>(RecipeManager.INSTANCE.getRecipeGroups());
            filterTabList(tabList, args.get(1));
            return tabList;
        }
        return Collections.singletonList("<recipe_name>");
    }

}
