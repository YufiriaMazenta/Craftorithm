package pers.yufiria.craftorithm.ui.editor.vanillasmithing;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmithingTransformEditorConfig;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.editor.RecipeEditor;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 锻造台配方编辑器
 * base(模板): 19, addition(添加物): 21, result: 24
 */
public final class VanillaSmithingTransformEditor extends RecipeEditor {

    private static final int TEMPLATE_SLOT = 19;
    private static final int BASE_SLOT = 21;
    private static final int ADDITION_SLOT = 23;
    private static final int RESULT_SLOT = 25;

    private final SmithingRecipe smithingRecipe;

    public VanillaSmithingTransformEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull SmithingRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.smithingRecipe = recipe;
        this.display = new MenuDisplay(
            VanillaSmithingTransformEditorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "X#######D",
                "######FFF",
                "#T#BCAFRK",
                "######FFF",
                "#########"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('K', this::getResultSaveAllComponentsIcon);
                layoutMap.put('F', this::getResultFrameIcon);
                layoutMap.put('C', this::getConfirmIcon);
                layoutMap.put('X', this::getBackIcon);
                layoutMap.put('D', this::getDeleteIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected void fillRecipeData(Inventory inventory) {
        var base = smithingRecipe.getBase();
        inventory.setItem(BASE_SLOT, base.getItemStack().clone());

        var addition = smithingRecipe.getAddition();
        inventory.setItem(ADDITION_SLOT, addition.getItemStack().clone());

        var template = ((SmithingTransformRecipe) smithingRecipe).getTemplate();
        inventory.setItem(TEMPLATE_SLOT, template.getItemStack().clone());

        ItemStack result = smithingRecipe.getResult();
        inventory.setItem(RESULT_SLOT, result.clone());
    }

    private Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaSmithingTransformEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaSmithingTransformEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(VanillaSmithingTransformEditorConfig.BACK_ICON.value());
    }

    private Icon getDeleteIcon() {
        return createDeleteIcon(VanillaSmithingTransformEditorConfig.DELETE_ICON.value());
    }

    @Override
    protected ConfigSectionConfig getResultSaveAllComponentsIconConfig() {
        return VanillaSmithingTransformEditorConfig.RESULT_SAVE_ALL_COMPONENTS_ICON;
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(VanillaSmithingTransformEditorConfig.CONFIRM_ICON.value());
        TranslatableIcon icon = new TranslatableIcon(iconDisplay);
        icon.setClickAction(event -> {
            Map<Integer, ItemStack> storedItems = StoredMenuUtils.storedItems(event);

            ItemStack result = storedItems.get(RESULT_SLOT);
            if (ItemHelper.isAir(result)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            String baseId = resolveIngredientId(storedItems.get(BASE_SLOT));
            String additionId = resolveIngredientId(storedItems.get(ADDITION_SLOT));
            String templateId = resolveIngredientId(storedItems.get(TEMPLATE_SLOT));
            NamespacedItemIdStack resultId = resolveResultId(result);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            saveEdit((Player) event.getWhoClicked(), configWrapper -> {
                configWrapper.set("type", SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM.typeKey());
                if (baseId != null) configWrapper.set("base", baseId);
                if (additionId != null) configWrapper.set("addition", additionId);
                if (templateId != null) configWrapper.set("template", templateId);
                configWrapper.set("result", resultId.toString());
            });
        });
        return icon;
    }
}
