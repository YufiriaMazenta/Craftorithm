package pers.yufiria.craftorithm.ui.editor.vanillacrafting;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaShapedEditorConfig;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.creator.vanillacrafting.RecipeBookCategoryIcon;
import pers.yufiria.craftorithm.ui.editor.RecipeEditor;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * 有序合成配方编辑器
 * 布局与VanillaShapedCreator一致，预填充现有配方数据。
 *
 * 材料槽位: 10,11,12 / 19,20,21 / 28,29,30 (3x3网格)
 * 结果槽位: 24
 * 返回按钮: 0
 * 确认按钮: 15
 */
public final class VanillaShapedEditor extends RecipeEditor {

    private static final int[] INGREDIENT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int RESULT_SLOT = 24;

    private final ShapedRecipe shapedRecipe;
    private final RecipeBookCategoryIcon categoryIcon;

    public VanillaShapedEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull ShapedRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.shapedRecipe = recipe;
        this.categoryIcon = new RecipeBookCategoryIcon(
            VanillaShapedEditorConfig.CATEGORY_ICON_MISC,
            VanillaShapedEditorConfig.CATEGORY_ICON_BUILDING,
            VanillaShapedEditorConfig.CATEGORY_ICON_REDSTONE,
            VanillaShapedEditorConfig.CATEGORY_ICON_EQUIPMENT
        );
        this.display = new MenuDisplay(
            VanillaShapedEditorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "B#######D",
                "#123#***#",
                "#456A* *#",
                "#789#***#",
                "####C####"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('*', this::getResultFrameIcon);
                layoutMap.put('A', this::getConfirmIcon);
                layoutMap.put('B', this::getBackIcon);
                layoutMap.put('C', () -> categoryIcon);
                layoutMap.put('D', this::getDeleteIcon);
                return layoutMap;
            })
        );
    }

    /**
     * 将现有配方数据填充到GUI槽位
     */
    @Override
    protected void fillRecipeData(Inventory inventory) {
        String[] shape = shapedRecipe.getShape();
        Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();

        for (int row = 0; row < shape.length && row < 3; row++) {
            String rowStr = shape[row];
            for (int col = 0; col < rowStr.length() && col < 3; col++) {
                char c = rowStr.charAt(col);
                int slotIndex = row * 3 + col;
                ItemStack ingredient = ingredientMap.get(c);
                if (ingredient != null) {
                    inventory.setItem(INGREDIENT_SLOTS[slotIndex], ingredient.clone());
                }
            }
        }

        ItemStack result = shapedRecipe.getResult();
        inventory.setItem(RESULT_SLOT, result.clone());

        categoryIcon.setCategory(shapedRecipe.getCategory());
    }

    private Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapedEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapedEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(VanillaShapedEditorConfig.BACK_ICON.value());
    }

    private Icon getDeleteIcon() {
        return createDeleteIcon(VanillaShapedEditorConfig.DELETE_ICON.value());
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(VanillaShapedEditorConfig.CONFIRM_ICON.value());
        TranslatableIcon icon = new TranslatableIcon(iconDisplay);
        icon.setClickAction(event -> {
            Map<Integer, ItemStack> storedItems = StoredMenuUtils.storedItems(event);

            // 验证结果物品
            ItemStack result = storedItems.get(RESULT_SLOT);
            if (ItemHelper.isAir(result)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 从3x3网格收集材料
            List<String> ingredientIds = new ArrayList<>();
            for (int slot : INGREDIENT_SLOTS) {
                ItemStack source = storedItems.get(slot);
                ingredientIds.add(ItemHelper.isAir(source) ? null : resolveIngredientId(source));
            }

            // 验证材料不全为空
            if (ingredientIds.stream().allMatch(Objects::isNull)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 解析结果物品ID
            NamespacedItemIdStack resultId = resolveResultId(result);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 构建形状与材料映射
            RecipeUtils.ShapedGrid shapedGrid = RecipeUtils.buildShapedGrid(ingredientIds);
            if (shapedGrid.shape().isEmpty()) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            saveEdit((Player) event.getWhoClicked(), configWrapper -> {
                configWrapper.set("type", SimpleRecipeTypes.VANILLA_SHAPED.typeKey());
                configWrapper.set("result", resultId.toString());
                configWrapper.set("shape", shapedGrid.shape());
                configWrapper.set("ingredients", shapedGrid.ingredientIdMap());
                configWrapper.set("recipe_book_category", categoryIcon.category().name().toLowerCase());
            });
        });
        return icon;
    }

}
