package com.github.yufiriamazenta.craftorithm.cmd.sub;

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
    private final Pattern recipeNamePattern = Pattern.compile("[a-z0-9/._-]+");

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
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        String recipeTypeStr = args.get(0).toLowerCase(Locale.ROOT);
        if (!recipeTypeList.contains(recipeTypeStr)) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE.value());
            return true;
        }
        String recipeName = args.get(1);
        Matcher matcher = recipeNamePattern.matcher(recipeName);
        if (!matcher.matches()) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME.value());
            return true;
        }
        if (RecipeManager.INSTANCE.hasCraftorithmRecipe(recipeName)) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_NAME_USED.value());
            return true;
        }
        RecipeType recipeType = RecipeType.valueOf(recipeTypeStr.toUpperCase(Locale.ROOT));
        Player player = (Player) sender;
        switch (recipeType) {
            case SHAPED:
            case SHAPELESS:
                new CraftingRecipeCreator(player, recipeType, recipeName).openMenu();
                break;
            case COOKING:
                new CookingRecipeCreator(player, recipeName).openMenu();
                break;
            case SMITHING:
                new SmithingRecipeCreator(player, recipeName).openMenu();
                break;
            case STONE_CUTTING:
                new StoneCuttingRecipeCreator(player, recipeName).openMenu();
                break;
            case POTION:
                new PotionMixCreator(player, recipeName).openMenu();
                break;
            case ANVIL:
                new AnvilRecipeCreator(player, recipeName).openMenu();
                break;
            default:
                LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE.value());
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
        }
        return Collections.singletonList("<recipe_name>");
    }

}
