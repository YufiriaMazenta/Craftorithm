package pers.yufiria.craftorithm.ui.editor.vanillacrafting;

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
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaShapelessEditorConfig;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.creator.vanillacrafting.RecipeBookCategoryIcon;
import pers.yufiria.craftorithm.ui.editor.RecipeEditor;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * 无序合成配方编辑器
 * 材料槽位: 10,11,12 / 19,20,21 / 28,29,30 (3x3网格)
 * 结果槽位: 24
 */
public final class VanillaShapelessEditor extends RecipeEditor {

    private static final int[] INGREDIENT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int RESULT_SLOT = 24;

    private final ShapelessRecipe shapelessRecipe;
    private final RecipeBookCategoryIcon categoryIcon;

    public VanillaShapelessEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull ShapelessRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.shapelessRecipe = recipe;
        this.categoryIcon = new RecipeBookCategoryIcon(
            VanillaShapelessEditorConfig.CATEGORY_ICON_MISC,
            VanillaShapelessEditorConfig.CATEGORY_ICON_BUILDING,
            VanillaShapelessEditorConfig.CATEGORY_ICON_REDSTONE,
            VanillaShapelessEditorConfig.CATEGORY_ICON_EQUIPMENT
        );
        this.display = new MenuDisplay(
            VanillaShapelessEditorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "B#######D",
                "#123#***#",
                "#456A* K#",
                "#789#***#",
                "####C####"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('K', this::getResultSaveAllComponentsIcon);
                layoutMap.put('*', this::getResultFrameIcon);
                layoutMap.put('A', this::getConfirmIcon);
                layoutMap.put('B', this::getBackIcon);
                layoutMap.put('C', () -> categoryIcon);
                layoutMap.put('D', this::getDeleteIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected void fillRecipeData(Inventory inventory) {
        List<ItemStack> ingredients = shapelessRecipe.getIngredientList();
        for (int i = 0; i < Math.min(ingredients.size(), INGREDIENT_SLOTS.length); i++) {
            inventory.setItem(INGREDIENT_SLOTS[i], ingredients.get(i).clone());
        }
        ItemStack result = shapelessRecipe.getResult();
        inventory.setItem(RESULT_SLOT, result.clone());

        categoryIcon.setCategory(shapelessRecipe.getCategory());
    }

    private Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapelessEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapelessEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(VanillaShapelessEditorConfig.BACK_ICON.value());
    }

    private Icon getDeleteIcon() {
        return createDeleteIcon(VanillaShapelessEditorConfig.DELETE_ICON.value());
    }

    @Override
    protected ConfigSectionConfig getResultSaveAllComponentsIconConfig() {
        return VanillaShapelessEditorConfig.RESULT_SAVE_ALL_COMPONENTS_ICON;
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(VanillaShapelessEditorConfig.CONFIRM_ICON.value());
        TranslatableIcon icon = new TranslatableIcon(iconDisplay);
        icon.setClickAction(event -> {
            Map<Integer, ItemStack> storedItems = StoredMenuUtils.storedItems(event);

            ItemStack result = storedItems.get(RESULT_SLOT);
            if (ItemHelper.isAir(result)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            List<String> ingredientIds = new ArrayList<>();
            for (int slot : INGREDIENT_SLOTS) {
                ItemStack source = storedItems.get(slot);
                if (!ItemHelper.isAir(source)) {
                    ingredientIds.add(resolveIngredientId(source));
                }
            }

            if (ingredientIds.isEmpty()) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            NamespacedItemIdStack resultId = resolveResultId(result);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            saveEdit((Player) event.getWhoClicked(), configWrapper -> {
                configWrapper.set("type", SimpleRecipeTypes.VANILLA_SHAPELESS.typeKey());
                configWrapper.set("result", resultId.toString());
                configWrapper.set("ingredients", ingredientIds);
                configWrapper.set("recipe_book_category", categoryIcon.category().name().toLowerCase());
            });
        });
        return icon;
    }
}
