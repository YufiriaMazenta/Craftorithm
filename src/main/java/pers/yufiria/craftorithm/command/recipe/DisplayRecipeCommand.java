package pers.yufiria.craftorithm.command.recipe;

import crypticlib.command.CommandInfo;
import crypticlib.command.CommandInvoker;
import crypticlib.command.CommandNode;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.perm.PermInfo;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.config.menu.display.VanillaStonecutting;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.ui.display.anvil.AnvilDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaBrewing.VanillaBrewingDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaShaped.VanillaShapedDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaShapeless.VanillaShapelessDisplayIconParser;
import pers.yufiria.craftorithm.ui.display.vanillaShapeless.VanillaShapelessDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaSmelting.VanillaSmeltingDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaSmithing.VanillaSmithingDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaStonecutting.VanillaStonecuttingDisplayMenuManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.ServerUtils;

import java.util.*;
import java.util.function.BiConsumer;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE)
    }
)
public class DisplayRecipeCommand extends CommandNode implements BukkitLifeCycleTask {

    public static final DisplayRecipeCommand INSTANCE = new DisplayRecipeCommand();
    private final Map<RecipeType, BiConsumer<Player, Recipe>> recipeDisplayMap = new RecipeTypeMap<>();

    protected DisplayRecipeCommand() {
        super(
            CommandInfo
                .builder("display")
                .permission(new PermInfo("craftorithm.command.display"))
                .usage("&r/craftorithm display <recipe_id>")
                .build()
        );
    }

    @Override
    public void execute(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(invoker);
            return;
        }
        Player target;
        if (args.size() >= 2) {
            String targetName = args.get(1);
            Player player = Bukkit.getPlayer(targetName);
            if (player == null) {
                //TODO 提示消息
                IOHelper.info("&cUnknown player: " + targetName);
                return;
            }
            target = player;
        } else {
            if (!CommandUtils.checkInvokerIsPlayer(invoker))
                return;
            target = (Player) invoker.asPlayer().getPlatformPlayer();
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(args.get(0));
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(namespacedKey);
        if (recipe == null) {
            return;
        }
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        recipeDisplayMap.getOrDefault(recipeType, (player, displayRecipe) -> {
            LangUtils.sendLang(player, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
        }).accept(target, recipe);
    }

    @Override
    public @Nullable List<String> tab(@NotNull CommandInvoker invoker, List<String> args) {
        if (args.size() <= 1) {
            Set<NamespacedKey> recipes = new LinkedHashSet<>(RecipeManager.INSTANCE.craftorithmRecipes().keySet());
            recipes.addAll(RecipeManager.INSTANCE.craftorithmRecipes().keySet());
            return recipes.stream().map(NamespacedKey::toString).toList();
        }
        return Collections.singletonList("");
    }

    public void addRecipeDisplay(RecipeType recipeType, BiConsumer<Player, Recipe> displayFunc) {
        recipeDisplayMap.put(recipeType, displayFunc);
    }

    public void removeRecipeDisplay(RecipeType recipeType) {
        recipeDisplayMap.remove(recipeType);
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        registerDefRecipeDisplay();
    }

    private void registerDefRecipeDisplay() {
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SHAPED, (player, recipe) -> {
            VanillaShapedDisplayMenuManager.INSTANCE.openMenu(player, (ShapedRecipe) recipe);
        });
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SHAPELESS, (player, recipe) -> {
            VanillaShapelessDisplayMenuManager.INSTANCE.openMenu(player, (ShapelessRecipe) recipe);
        });
        BiConsumer<Player, Recipe> smeltingFunc = (player, recipe) -> {
            VanillaSmeltingDisplayMenuManager.INSTANCE.openMenu(player, (CookingRecipe<?>) recipe);
        };
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, smeltingFunc);
        BiConsumer<Player, Recipe> smithingFunc = (player, recipe) -> {
            VanillaSmithingDisplayMenuManager.INSTANCE.openMenu(player, (SmithingRecipe) recipe);
        };
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM, smithingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_STONECUTTING, (player, recipe) -> {
            VanillaStonecuttingDisplayMenuManager.INSTANCE.openMenu(player, (StonecuttingRecipe) recipe);
        });
        IOHelper.info("Support potion mix: " + ServerUtils.supportPotionMix());
        if (ServerUtils.supportPotionMix()) {
            addRecipeDisplay(SimpleRecipeTypes.VANILLA_BREWING, (player, recipe) -> {
                VanillaBrewingDisplayMenuManager.INSTANCE.openMenu(player, (BrewingRecipe) recipe);
            });
        }
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            addRecipeDisplay(SimpleRecipeTypes.ANVIL, (player, recipe) -> {
                AnvilDisplayMenuManager.INSTANCE.openMenu(player, (AnvilRecipe) recipe);
            });
        }
    }


}
