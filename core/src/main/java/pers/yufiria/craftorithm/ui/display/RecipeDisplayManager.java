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
import pers.yufiria.craftorithm.ui.display.anvil.AnvilDisplayMenu;
import pers.yufiria.craftorithm.ui.display.vanillaBrewing.VanillaBrewingDisplayMenu;
import pers.yufiria.craftorithm.ui.display.vanillaShaped.VanillaShapedDisplayMenu;
import pers.yufiria.craftorithm.ui.display.vanillaShapeless.VanillaShapelessDisplayMenu;
import pers.yufiria.craftorithm.ui.display.vanillaSmelting.VanillaSmeltingDisplayMenu;
import pers.yufiria.craftorithm.ui.display.vanillaSmithing.VanillaSmithingDisplayMenu;
import pers.yufiria.craftorithm.ui.display.vanillaStonecutting.VanillaStonecuttingDisplayMenu;
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
            VanillaShapedDisplayMenu shapedDisplayMenu = new VanillaShapedDisplayMenu(player, (ShapedRecipe) recipe);
            shapedDisplayMenu.openMenu();
            return shapedDisplayMenu;
        });
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SHAPELESS, (player, recipe) -> {
            VanillaShapelessDisplayMenu shapelessDisplayMenu = new VanillaShapelessDisplayMenu(player, (ShapelessRecipe) recipe);
            shapelessDisplayMenu.openMenu();
            return shapelessDisplayMenu;
        });
        BiFunction<Player, Recipe, Menu> smeltingFunc = (player, recipe) -> {
            VanillaSmeltingDisplayMenu smeltingDisplayMenu = new VanillaSmeltingDisplayMenu(player, (CookingRecipe<?>) recipe);
            smeltingDisplayMenu.openMenu();
            return smeltingDisplayMenu;
        };
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, smeltingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, smeltingFunc);
        BiFunction<Player, Recipe, Menu> smithingFunc = (player, recipe) -> {
            VanillaSmithingDisplayMenu smithingDisplayMenu = new VanillaSmithingDisplayMenu(player, (SmithingRecipe) recipe);
            smithingDisplayMenu.openMenu();
            return smithingDisplayMenu;
        };
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM, smithingFunc);
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_STONECUTTING, (player, recipe) -> {
            VanillaStonecuttingDisplayMenu stonecuttingDisplayMenu = new VanillaStonecuttingDisplayMenu(player, (StonecuttingRecipe) recipe);
            stonecuttingDisplayMenu.openMenu();
            return stonecuttingDisplayMenu;
        });
        IOHelper.info("Support potion mix: " + ServerUtils.supportPotionMix());
        if (ServerUtils.supportPotionMix()) {
            addRecipeDisplay(SimpleRecipeTypes.VANILLA_BREWING, (player, recipe) -> {
                VanillaBrewingDisplayMenu vanillaBrewingDisplay = new VanillaBrewingDisplayMenu(player, (BrewingRecipe) recipe);
                vanillaBrewingDisplay.openMenu();
                return vanillaBrewingDisplay;
            });
        }
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            addRecipeDisplay(SimpleRecipeTypes.ANVIL, (player, recipe) -> {
                AnvilDisplayMenu anvilDisplayMenu = new AnvilDisplayMenu(player, (AnvilRecipe) recipe);
                anvilDisplayMenu.openMenu();
                return anvilDisplayMenu;
            });
        }
    }


}

