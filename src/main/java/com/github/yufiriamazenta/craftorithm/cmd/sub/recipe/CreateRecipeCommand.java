package com.github.yufiriamazenta.craftorithm.cmd.sub.recipe;

import com.github.yufiriamazenta.craftorithm.cmd.sub.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.menu.creator.*;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroupParser;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.CrypticLib;
import crypticlib.function.TernaryFunction;
import crypticlib.ui.menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.yufiriamazenta.craftorithm.recipe.RecipeType.*;

public final class CreateRecipeCommand extends AbstractSubCommand {

    public static final CreateRecipeCommand INSTANCE = new CreateRecipeCommand();
    private final Pattern recipeNamePattern = Pattern.compile("^[a-z0-9._-]+$");
    private static final Map<RecipeType, TernaryFunction<Player, String, String, Menu>> CREATOR_MAP = new ConcurrentHashMap<>();
    private static final List<String> RECIPE_TYPE_LIST = new CopyOnWriteArrayList<>();

    static {
        addRecipeCreatorSupplier(SHAPED, (player, groupName, recipeName) -> new CraftingRecipeCreator(player, SHAPED, groupName, recipeName));
        addRecipeCreatorSupplier(SHAPELESS, (player, groupName, recipeName) -> new CraftingRecipeCreator(player, SHAPELESS, groupName, recipeName));
        if (CrypticLib.minecraftVersion() >= 11400) {
            addRecipeCreatorSupplier(COOKING, CookingRecipeCreator::new);
            addRecipeCreatorSupplier(SMITHING, SmithingRecipeCreator::new);
            addRecipeCreatorSupplier(STONE_CUTTING, StoneCuttingRecipeCreator::new);
        }

        if (RecipeManager.INSTANCE.supportPotionMix()) {
            addRecipeCreatorSupplier(POTION, PotionMixCreator::new);
        }

        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            addRecipeCreatorSupplier(ANVIL, AnvilRecipeCreator::new);
        }
    }

    private CreateRecipeCommand() {
        super("create", "craftorithm.command.create");
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
        if (!RECIPE_TYPE_LIST.contains(recipeTypeStr)) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return true;
        }
        String groupName = args.size() < 2 ? "default" : args.get(1);
        if (RecipeGroupParser.GLOBAL_KEYS.contains(groupName)) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_NAME_USED);
            return true;
        }

        String recipeName = args.size() < 3 ? UUID.randomUUID().toString() : args.get(2);
        Matcher matcher = recipeNamePattern.matcher(groupName);
        if (!matcher.matches()) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
            return true;
        }
        matcher = recipeNamePattern.matcher(recipeName);
        if (!matcher.matches()) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
            return true;
        }

        RecipeGroup recipeGroup = RecipeManager.INSTANCE.getRecipeGroup(groupName);
        if (recipeGroup != null) {
            if (recipeGroup.groupRecipeKeyMap().containsKey(recipeName)) {
                LangUtil.sendLang(sender, Languages.COMMAND_CREATE_NAME_USED);
                return true;
            }
        }

        RecipeType recipeType = RecipeType.getByName(recipeTypeStr);
        Player player = (Player) sender;
        Menu recipeCreator = CREATOR_MAP.getOrDefault(recipeType, (a, b, c) -> null).apply(player, groupName, recipeName);
        if (recipeCreator == null) {
            LangUtil.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return true;
        }
        recipeCreator.openMenu();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> tabList = new ArrayList<>(RECIPE_TYPE_LIST);
            filterTabList(tabList, args.get(0));
            return tabList;
        } else if (args.size() == 2) {
            List<String> tabList = new ArrayList<>(RecipeManager.INSTANCE.getRecipeGroups());
            tabList.add("default");
            filterTabList(tabList, args.get(1));
            return tabList;
        }
        return Collections.singletonList("<recipe_name>");
    }

    public static TernaryFunction<Player, String, String, Menu> addRecipeCreatorSupplier(RecipeType recipeType, TernaryFunction<Player, String, String, Menu> creatorSupplier) {
        RECIPE_TYPE_LIST.add(recipeType.typeId().toLowerCase());
        return CREATOR_MAP.put(recipeType, creatorSupplier);
    }

}
