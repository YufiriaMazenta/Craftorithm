package pers.yufiria.craftorithm.ui.editor;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.ui.menu.Menu;
import crypticlib.util.TriFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.*;
import pers.yufiria.craftorithm.ui.SmeltingMenuType;
import pers.yufiria.craftorithm.ui.editor.anvil.AnvilEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaBrewing.VanillaBrewingEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaCrafting.VanillaShapedEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaCrafting.VanillaShapelessEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaSmelting.SmeltingEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaSmithing.VanillaSmithingTransformEditor;
import pers.yufiria.craftorithm.ui.editor.vanillaStonecutting.VanillaStonecuttingEditor;

import java.util.Optional;

/**
 * 配方编辑器管理器
 * 管理各配方类型的编辑器注册，类似RecipeDisplayManager
 */
@LifecycleTaskSettings(
    rules = {
        @LifecycleRule(lifeCycle = Lifecycle.ENABLE)
    }
)
public enum RecipeEditorManager implements LifecycleTask {

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
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
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

        for (SmeltingMenuType smeltingType : SmeltingMenuType.values()) {
            registerEditor(smeltingType.recipeType(), (player, recipeKey, recipe) -> {
                SmeltingEditor editor = new SmeltingEditor(player, recipeKey, (CookingRecipe<?>) recipe, smeltingType);
                editor.openMenu();
                return editor;
            });
        }

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

        if (RecipeManager.INSTANCE.supportPotionMix()) {
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
