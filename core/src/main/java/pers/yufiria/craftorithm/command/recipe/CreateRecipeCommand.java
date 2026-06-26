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
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.anvil.AnvilCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaBrewing.VanillaBrewingCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaCrafting.VanillaShapedCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaCrafting.VanillaShapelessCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaSmelting.VanillaSmeltingBlastCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaSmelting.VanillaSmeltingCampfireCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaSmelting.VanillaSmeltingFurnaceCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaSmelting.VanillaSmeltingSmokerCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaSmithing.VanillaSmithingTransformCreator;
import pers.yufiria.craftorithm.ui.creator.vanillaStonecutting.VanillaStonecuttingCreator;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE)
    }
)
public final class CreateRecipeCommand extends CommandNode implements BukkitLifeCycleTask {

    public static final CreateRecipeCommand INSTANCE = new CreateRecipeCommand();
    private final Pattern RECIPE_ID_PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private final Map<RecipeType, RecipeCreatorFactory> recipeCreatorMap = new RecipeTypeMap<>();

    private CreateRecipeCommand() {
        super(CommandInfo
            .builder("create")
            .permission(new PermInfo("craftorithm.command.create"))
            .usage("&r/craftorithm create <recipe_type> [recipe_id] [recipe_file_name]")
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
        String recipeTypeStr = args.getFirst();
        String recipeId = null;
        String recipeFileName = null;

        if (args.size() >= 2) {
            recipeId = args.get(1);
            Matcher matcher = RECIPE_ID_PATTERN.matcher(recipeId);
            if (!matcher.matches()) {
                LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
                return;
            }
            if (RecipeManager.INSTANCE.containsRecipe(recipeId)) {
                LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_NAME_USED);
                return;
            }
        }
        if (args.size() >= 3) {
            recipeFileName = args.get(2);
        }

        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipeTypeStr);
        if (recipeType == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        RecipeCreatorFactory creatorFactory = recipeCreatorMap.get(recipeType);
        if (creatorFactory == null) {
            LangUtils.sendLang(invoker, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        Player player = (Player) invoker.asPlayer().getPlatformPlayer();
        creatorFactory.create(player, recipeId, recipeFileName);
    }

    @Override
    public List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            return recipeCreatorMap.keySet().stream().map(RecipeType::typeKey).toList();
        }
        if (args.size() == 2) {
            return Collections.singletonList("<recipe_id>");
        }
        return Collections.singletonList("<recipe_file_name>");
    }

    private void registerDefRecipeCreators() {
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SHAPED, (player, recipeId, recipeFileName) -> {
            new VanillaShapedCreator(player, recipeId, recipeFileName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SHAPELESS, (player, recipeId, recipeFileName) -> {
            new VanillaShapelessCreator(player, recipeId, recipeFileName).openMenu();
        });
        // 熔炉配方 (furnace, blast, smoker, campfire)
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, (player, recipeId, recipeFileName) -> {
            new VanillaSmeltingFurnaceCreator(player, recipeId, recipeFileName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, (player, recipeId, recipeFileName) -> {
            new VanillaSmeltingBlastCreator(player, recipeId, recipeFileName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, (player, recipeId, recipeFileName) -> {
            new VanillaSmeltingSmokerCreator(player, recipeId, recipeFileName).openMenu();
        });
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, (player, recipeId, recipeFileName) -> {
            new VanillaSmeltingCampfireCreator(player, recipeId, recipeFileName).openMenu();
        });
        // 锻造台配方
        addRecipeCreator(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM, (player, recipeId, recipeFileName) -> {
            new VanillaSmithingTransformCreator(player, recipeId, recipeFileName).openMenu();
        });
        // 切石机配方
        addRecipeCreator(SimpleRecipeTypes.VANILLA_STONECUTTING, (player, recipeId, recipeFileName) -> {
            new VanillaStonecuttingCreator(player, recipeId, recipeFileName).openMenu();
        });
        // 酿造台配方
        addRecipeCreator(SimpleRecipeTypes.VANILLA_BREWING, (player, recipeId, recipeFileName) -> {
            new VanillaBrewingCreator(player, recipeId, recipeFileName).openMenu();
        });
        // 铁砧配方
        addRecipeCreator(SimpleRecipeTypes.ANVIL, (player, recipeId, recipeFileName) -> {
            new AnvilCreator(player, recipeId, recipeFileName).openMenu();
        });
    }

    public void addRecipeCreator(RecipeType recipeType, RecipeCreatorFactory creatorFunc) {
        recipeCreatorMap.put(recipeType, creatorFunc);
    }

    public void removeRecipeCreator(RecipeType recipeType) {
        recipeCreatorMap.remove(recipeType);
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        registerDefRecipeCreators();
    }

    @FunctionalInterface
    public interface RecipeCreatorFactory {
        void create(Player player, @Nullable String recipeId, @Nullable String recipeFileName);
    }

}
