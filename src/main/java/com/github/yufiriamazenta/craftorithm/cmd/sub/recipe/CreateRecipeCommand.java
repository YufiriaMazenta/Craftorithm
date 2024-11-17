package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.menu.creator.*;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.CommandUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CreateRecipeCommand extends BukkitSubcommand {

    public static final CreateRecipeCommand INSTANCE = new CreateRecipeCommand();
    private final List<String> recipeTypeList;
    private final Pattern recipeNamePattern = Pattern.compile("^[a-z0-9._-]+$");

    private CreateRecipeCommand() {
        super(CommandInfo
            .builder("create")
            .permission(new PermInfo("craftorithm.command.create"))
            .usage("&r/craftorithm create <recipe_type> [recipe_name]")
            .build()
        );
        recipeTypeList = Arrays.stream(RecipeType.values()).map(RecipeType::name).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        List<String> unsupportedRecipeTypeList = new ArrayList<>();
        unsupportedRecipeTypeList.add("random_cooking");
        unsupportedRecipeTypeList.add("unknown");
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            unsupportedRecipeTypeList.add("potion");
        }
        if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
            unsupportedRecipeTypeList.add("anvil");
        recipeTypeList.removeAll(unsupportedRecipeTypeList);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!CommandUtils.checkSenderIsPlayer(sender))
            return;
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        String recipeTypeStr = args.get(0).toLowerCase(Locale.ROOT);
        if (!recipeTypeList.contains(recipeTypeStr)) {
            LangUtils.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        String recipeName;
        if (args.size() < 2)
            recipeName = UUID.randomUUID().toString();
        else
            recipeName = args.get(1);

        Matcher matcher = recipeNamePattern.matcher(recipeName);
        if (!matcher.matches()) {
            LangUtils.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
            return;
        }
        if (RecipeManager.INSTANCE.hasCraftorithmRecipe(recipeName)) {
            LangUtils.sendLang(sender, Languages.COMMAND_CREATE_NAME_USED);
            return;
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
                LangUtils.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
                break;
        }
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            return new ArrayList<>(recipeTypeList);
        }
        return Collections.singletonList("<recipe_name>");
    }

}
