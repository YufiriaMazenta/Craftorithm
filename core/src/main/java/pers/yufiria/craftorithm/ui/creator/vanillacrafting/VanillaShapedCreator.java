package pers.yufiria.craftorithm.ui.creator.vanillacrafting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.creator.VanillaShapedCreatorConfig;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.*;
import java.util.function.Supplier;

public final class VanillaShapedCreator extends RecipeCreator {

    /**
     * 3x3合成网格对应的GUI槽位:
     * 行0: 10, 11, 12
     * 行1: 19, 20, 21
     * 行2: 28, 29, 30
     */
    private static final int[] INGREDIENT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int RESULT_SLOT = 24;

    public VanillaShapedCreator(@NotNull Player player, @Nullable String recipeId, @Nullable String recipeFileName) {
        super(player, recipeId, recipeFileName);
        this.display = new MenuDisplay(
            VanillaShapedCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
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
                layoutMap.put('C', () -> new RecipeBookCategoryIcon(
                    VanillaShapedCreatorConfig.CATEGORY_ICON_MISC,
                    VanillaShapedCreatorConfig.CATEGORY_ICON_BUILDING,
                    VanillaShapedCreatorConfig.CATEGORY_ICON_REDSTONE,
                    VanillaShapedCreatorConfig.CATEGORY_ICON_EQUIPMENT
                ));
                return layoutMap;
            })
        );
    }

    @Override
    protected RecipeType recipeType() {
        return SimpleRecipeTypes.VANILLA_SHAPED;
    }

    @Override
    protected Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapedCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapedCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }

    @Override
    protected ConfigSectionConfig getResultSaveAllComponentsIconConfig() {
        return VanillaShapedCreatorConfig.RESULT_SAVE_ALL_COMPONENTS_ICON;
    }

    /**
     * 确认创建配方按钮
     * 将GUI中存储的物品解析为配方配置并保存、注册
     */
    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(
            VanillaShapedCreatorConfig.CONFIRM_ICON.value()
        );
        TranslatableIcon icon = new TranslatableIcon(iconDisplay);
        icon.setClickAction(event -> {
            Map<Integer, ItemStack> storedItems = StoredMenuUtils.storedItems(event);

            // 1. 验证结果物品
            ItemStack result = storedItems.get(RESULT_SLOT);
            if (ItemHelper.isAir(result)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 2. 从3x3网格收集材料
            List<String> ingredientIds = new ArrayList<>();
            for (int slot : INGREDIENT_SLOTS) {
                ItemStack source = storedItems.get(slot);
                ingredientIds.add(ItemHelper.isAir(source) ? null : resolveIngredientId(source));
            }

            // 3. 检查是否所有材料为空
            if (ingredientIds.stream().allMatch(Objects::isNull)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 4. 解析结果物品ID
            NamespacedItemIdStack resultId = resolveResultId(result);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 5. 构建形状与材料映射
            RecipeUtils.ShapedGrid shapedGrid = RecipeUtils.buildShapedGrid(ingredientIds);
            if (shapedGrid.shape().isEmpty()) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 6. 获取配方书分类
            Icon categoryIconOrNull = getIcon(40).orElse(null);
            CraftingBookCategory category = categoryIconOrNull instanceof RecipeBookCategoryIcon categoryIcon
                ? categoryIcon.category()
                : CraftingBookCategory.MISC;

            // 7. 创建并保存配方配置文件
            saveAndRegister((Player) event.getWhoClicked(), resultId, recipeConfig -> {
                recipeConfig.set("shape", shapedGrid.shape());
                recipeConfig.set("ingredients", shapedGrid.ingredientIdMap());
                recipeConfig.set("recipe_book_category", category.name().toLowerCase());
            });
        });
        return icon;
    }

}