package pers.yufiria.craftorithm.ui.editor;

import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.menu.Menu;
import crypticlib.util.TriFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.ui.editor.anvil.AnvilEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaBrewing.VanillaBrewingEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaCrafting.VanillaShapedEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaCrafting.VanillaShapelessEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaSmelting.VanillaSmeltingBlastEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaSmelting.VanillaSmeltingCampfireEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaSmelting.VanillaSmeltingFurnaceEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaSmelting.VanillaSmeltingSmokerEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaSmithing.VanillaSmithingTransformEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaStonecutting.VanillaStonecuttingEditor;
import pers.yufiria.craftorithm.util.ServerUtils;

import java.util.Optional;

/**
 * 配方编辑器管理器
 * 管理各配方类型的编辑器注册，类似RecipeDisplayManager
 */
@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE)
    }
)
public enum RecipeEditorManager implements BukkitLifeCycleTask {

    INSTANCE;

    private final RecipeTypeMap<RecipeType, TriFunction<Player, NamespacedKey, Recipe, Menu>> editorMap = new RecipeTypeMap<>();

    public void registerEditor(RecipeType recipeType, TriFunction<Player, NamespacedKey, Recipe, Menu> editorFunc) {
        editorMap.put(recipeType, editorFunc);
    }

    public void removeEditor(RecipeType recipeType) {
        editorMap.remove(recipeType);
    }

    public Optional<TriFunction<Player, NamespacedKey, Recipe, Menu>> getEditor(RecipeType recipeType) {
        return Optional.ofNullable(editorMap.get(recipeType));
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        registerDefaultEditors();
    }

    private void registerDefaultEditors() {
        registerEditor(SimpleRecipeTypes.VANILLA_SHAPED, (player, recipeKey, recipe) -> {
            VanillaShapedEditor editor = new VanillaShapedEditor(player, recipeKey, (ShapedRecipe) recipe);
            editor.openMenu();
            return editor;
        });

        registerEditor(SimpleRecipeTypes.VANILLA_SHAPELESS, (player, recipeKey, recipe) -> {
            VanillaShapelessEditor editor = new VanillaShapelessEditor(player, recipeKey, (ShapelessRecipe) recipe);
            editor.openMenu();
            return editor;
        });

        registerEditor(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, (player, recipeKey, recipe) -> {
            VanillaSmeltingFurnaceEditor editor = new VanillaSmeltingFurnaceEditor(player, recipeKey, (CookingRecipe<?>) recipe);
            editor.openMenu();
            return editor;
        });
        registerEditor(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, (player, recipeKey, recipe) -> {
            VanillaSmeltingBlastEditor editor = new VanillaSmeltingBlastEditor(player, recipeKey, (CookingRecipe<?>) recipe);
            editor.openMenu();
            return editor;
        });
        registerEditor(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, (player, recipeKey, recipe) -> {
            VanillaSmeltingSmokerEditor editor = new VanillaSmeltingSmokerEditor(player, recipeKey, (CookingRecipe<?>) recipe);
            editor.openMenu();
            return editor;
        });
        registerEditor(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, (player, recipeKey, recipe) -> {
            VanillaSmeltingCampfireEditor editor = new VanillaSmeltingCampfireEditor(player, recipeKey, (CookingRecipe<?>) recipe);
            editor.openMenu();
            return editor;
        });

        registerEditor(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM, (player, recipeKey, recipe) -> {
            VanillaSmithingTransformEditor editor = new VanillaSmithingTransformEditor(player, recipeKey, (org.bukkit.inventory.SmithingRecipe) recipe);
            editor.openMenu();
            return editor;
        });

        registerEditor(SimpleRecipeTypes.VANILLA_STONECUTTING, (player, recipeKey, recipe) -> {
            VanillaStonecuttingEditor editor = new VanillaStonecuttingEditor(player, recipeKey, (StonecuttingRecipe) recipe);
            editor.openMenu();
            return editor;
        });

        if (ServerUtils.supportPotionMix()) {
            registerEditor(SimpleRecipeTypes.VANILLA_BREWING, (player, recipeKey, recipe) -> {
                VanillaBrewingEditor editor = new VanillaBrewingEditor(player, recipeKey, (BrewingRecipe) recipe);
                editor.openMenu();
                return editor;
            });
        }

        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            registerEditor(SimpleRecipeTypes.ANVIL, (player, recipeKey, recipe) -> {
                AnvilEditor editor = new AnvilEditor(player, recipeKey, (AnvilRecipe) recipe);
                editor.openMenu();
                return editor;
            });
        }
    }

}
