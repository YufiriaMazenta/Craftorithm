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
import pers.yufiria.craftorithm.config.menu.creator.VanillaShapelessCreatorConfig;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.Supplier;

public class VanillaShapelessCreator extends RecipeCreator {

    /**
     * 3x3合成网格对应的GUI槽位:
     * 行0: 10, 11, 12
     * 行1: 19, 20, 21
     * 行2: 28, 29, 30
     */
    private static final int[] INGREDIENT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int RESULT_SLOT = 24;

    public VanillaShapelessCreator(@NotNull Player player, @Nullable String recipeId, @Nullable String recipeFileName) {
        super(player, recipeId, recipeFileName);
        this.display = new MenuDisplay(
            VanillaShapelessCreatorConfig.TITLE.value(),
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
                    VanillaShapelessCreatorConfig.CATEGORY_ICON_MISC,
                    VanillaShapelessCreatorConfig.CATEGORY_ICON_BUILDING,
                    VanillaShapelessCreatorConfig.CATEGORY_ICON_REDSTONE,
                    VanillaShapelessCreatorConfig.CATEGORY_ICON_EQUIPMENT
                ));
                return layoutMap;
            })
        );
    }

    @Override
    protected RecipeType recipeType() {
        return SimpleRecipeTypes.VANILLA_SHAPELESS;
    }

    @Override
    protected Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapelessCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaShapelessCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }

    @Override
    protected ConfigSectionConfig getResultSaveAllComponentsIconConfig() {
        return VanillaShapelessCreatorConfig.RESULT_SAVE_ALL_COMPONENTS_ICON;
    }

    /**
     * 确认创建配方按钮
     * 将GUI中存储的物品解析为配方配置并保存、注册
     */
    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(
            VanillaShapelessCreatorConfig.CONFIRM_ICON.value()
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
                if (!ItemHelper.isAir(source)) {
                    ingredientIds.add(resolveIngredientId(source));
                }
            }

            // 3. 检查是否所有材料为空
            if (ingredientIds.isEmpty() || ingredientIds.stream().allMatch(Objects::isNull)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 4. 解析结果物品ID
            NamespacedItemIdStack resultId = resolveResultId(result);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 5. 获取配方书分类
            Icon categoryIconOrNull = getIcon(40).orElse(null);
            CraftingBookCategory category = categoryIconOrNull instanceof RecipeBookCategoryIcon categoryIcon
                ? categoryIcon.category()
                : CraftingBookCategory.MISC;

            // 6. 创建并保存配方配置文件
            saveAndRegister((Player) event.getWhoClicked(), resultId, recipeConfig -> {
                recipeConfig.set("ingredients", ingredientIds);
                recipeConfig.set("recipe_book_category", category.name().toLowerCase());
            });
        });
        return icon;
    }

}
