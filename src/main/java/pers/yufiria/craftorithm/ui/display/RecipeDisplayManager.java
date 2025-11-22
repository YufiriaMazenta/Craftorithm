package pers.yufiria.craftorithm.ui.display;

import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.menu.Menu;
import crypticlib.util.IOHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.ui.display.anvil.AnvilDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaBrewing.VanillaBrewingDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaShaped.VanillaShapedDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaShapeless.VanillaShapelessDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaSmelting.VanillaSmeltingDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaSmithing.VanillaSmithingDisplayMenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaStonecutting.VanillaStonecuttingDisplayMenuManager;
import pers.yufiria.craftorithm.util.ServerUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE)
    }
)
public enum RecipeDisplayManager implements BukkitLifeCycleTask {

    INSTANCE;
    private final Map<RecipeType, BiFunction<Player, Recipe, Menu>> recipeDisplayMap = new RecipeTypeMap<>();

    public void addRecipeDisplay(RecipeType recipeType, BiFunction<Player, Recipe, Menu> displayFunc) {
        recipeDisplayMap.put(recipeType, displayFunc);
    }

    public void removeRecipeDisplay(RecipeType recipeType) {
        recipeDisplayMap.remove(recipeType);
    }

    public Optional<BiFunction<Player, Recipe, Menu>> getRecipeDisplay(RecipeType recipeType) {
        return Optional.ofNullable(recipeDisplayMap.get(recipeType));
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        registerDefRecipeDisplay();
    }

    private void registerDefRecipeDisplay() {
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SHAPED, (player, recipe) -> {
            return VanillaShapedDisplayMenuManager.INSTANCE.openMenu(player, (ShapedRecipe) recipe);
        });
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SHAPELESS, (player, recipe) -> {
            return VanillaShapelessDisplayMenuManager.INSTANCE.openMenu(player, (ShapelessRecipe) recipe);
        });
        BiFunction<Player, Recipe, Menu> smeltingFunc = (player, recipe) -> {
            return VanillaSmeltingDisplayMenuManager.INSTANCE.openMenu(player, (CookingRecipe<?>) recipe);
        };
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, smeltingFunc);
        BiFunction<Player, Recipe, Menu> smithingFunc = (player, recipe) -> {
            return VanillaSmithingDisplayMenuManager.INSTANCE.openMenu(player, (SmithingRecipe) recipe);
        };
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM, smithingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_STONECUTTING, (player, recipe) -> {
            return VanillaStonecuttingDisplayMenuManager.INSTANCE.openMenu(player, (StonecuttingRecipe) recipe);
        });
        IOHelper.info("Support potion mix: " + ServerUtils.supportPotionMix());
        if (ServerUtils.supportPotionMix()) {
            addRecipeDisplay(SimpleRecipeTypes.VANILLA_BREWING, (player, recipe) -> {
                return VanillaBrewingDisplayMenuManager.INSTANCE.openMenu(player, (BrewingRecipe) recipe);
            });
        }
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            addRecipeDisplay(SimpleRecipeTypes.ANVIL, (player, recipe) -> {
                return AnvilDisplayMenuManager.INSTANCE.openMenu(player, (AnvilRecipe) recipe);
            });
        }
    }


}

