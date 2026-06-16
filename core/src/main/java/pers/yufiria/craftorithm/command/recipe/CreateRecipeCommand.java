package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.perm.PermInfo;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.anvil.AnvilCreator;
import pers.yufiria.craftorithm.ui.creator.brewing.VanillaBrewingCreator;
import pers.yufiria.craftorithm.ui.creator.crafting.VanillaShapedCreator;
import pers.yufiria.craftorithm.ui.creator.crafting.VanillaShapelessCreator;
import pers.yufiria.craftorithm.ui.creator.smelting.VanillaSmeltingBlastCreator;
import pers.yufiria.craftorithm.ui.creator.smelting.VanillaSmeltingCampfireCreator;
import pers.yufiria.craftorithm.ui.creator.smelting.VanillaSmeltingFurnaceCreator;
import pers.yufiria.craftorithm.ui.creator.smelting.VanillaSmeltingSmokerCreator;
import pers.yufiria.craftorithm.ui.creator.smithing.VanillaSmithingCreator;
import pers.yufiria.craftorithm.ui.creator.stonecutting.VanillaStonecuttingCreator;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE)
    }
)
public final class CreateRecipeCommand extends CommandNode implements BukkitLifeCycleTask {

    public static final CreateRecipeCommand INSTANCE = new CreateRecipeCommand();
    private final Pattern recipeNamePattern = Pattern.compile("^[a-z0-9._-]+$");
    private final Map<RecipeType, BiConsumer<Player, String>> recipeCreatorMap = new RecipeTypeMap<>();

    private CreateRecipeCommand() {
        super(CommandInfo
            .builder("create")
            .permission(new PermInfo("craftorithm.command.create"))
            .usage("&r/craftorithm create <recipe_type> [recipe_name]")
            .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        if (!CommandUtils.checkInvokerIsPlayer(invoker))
            return;
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        String recipeTypeStr = args.get(0);
        String recipeName;
        if (args.size() < 2)
            recipeName = null;
        else {
            recipeName = args.get(1);
            Matcher matcher = recipeNamePattern.matcher(recipeName);
            if (!matcher.matches()) {
                LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
                return;
            }
            if (RecipeManager.INSTANCE.containsRecipe(recipeName)) {
                LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_NAME_USED);
                return;
            }
        }

        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipeTypeStr);
        if (recipeType == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        BiConsumer<Player, String> creatorConsumer = recipeCreatorMap.get(recipeType);
        if (creatorConsumer == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        Player player = (Player) invoker.asPlayer().getPlatformPlayer();
        creatorConsumer.accept(player, recipeName);
    }

    @Override
    public List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return recipeCreatorMap.keySet().stream().map(RecipeType::typeKey).toList();
        }
        return Collections.singletonList("<recipe_name>");
    }

    private void registerDefRecipeCreators() {
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SHAPED, (player, recipeName) -> {
            new VanillaShapedCreator(player, recipeName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SHAPELESS, (player, recipeName) -> {
            new VanillaShapelessCreator(player, recipeName).openMenu();
        });
        // 熔炉配方 (furnace, blast, smoker, campfire)
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, (player, recipeName) -> {
            new VanillaSmeltingFurnaceCreator(player, recipeName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, (player, recipeName) -> {
            new VanillaSmeltingBlastCreator(player, recipeName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, (player, recipeName) -> {
            new VanillaSmeltingSmokerCreator(player, recipeName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, (player, recipeName) -> {
            new VanillaSmeltingCampfireCreator(player, recipeName).openMenu();
        });
        // 锻造台配方
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM, (player, recipeName) -> {
            new VanillaSmithingCreator(player, recipeName).openMenu();
        });
        // 切石机配方
        addRecipeCreator(SimpleRecipeTypes.VANILLA_STONECUTTING, (player, recipeName) -> {
            new VanillaStonecuttingCreator(player, recipeName).openMenu();
        });
        // 酿造台配方
        addRecipeCreator(SimpleRecipeTypes.VANILLA_BREWING, (player, recipeName) -> {
            new VanillaBrewingCreator(player, recipeName).openMenu();
        });
        // 铁砧配方
        addRecipeCreator(SimpleRecipeTypes.ANVIL, (player, recipeName) -> {
            new AnvilCreator(player, recipeName).openMenu();
        });
    }

    public void addRecipeCreator(RecipeType recipeType, BiConsumer<Player, String> creatorFunc) {
        recipeCreatorMap.put(recipeType, creatorFunc);
    }

    public void removeRecipeCreator(RecipeType recipeType) {
        recipeCreatorMap.remove(recipeType);
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        registerDefRecipeCreators();
    }

}
