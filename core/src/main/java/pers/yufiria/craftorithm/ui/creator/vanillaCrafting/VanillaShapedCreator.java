package pers.yufiria.craftorithm.ui.creator.vanillaCrafting;

import crypticlib.config.BukkitConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.creator.VanillaShapedCreatorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

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

    public VanillaShapedCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
        this.display = new MenuDisplay(
            VanillaShapedCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "#123#***#",
                "#456A* *#",
                "#789#***#",
                "####C####"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
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

    /**
     * 移除配方形状中的全空白列
     */
    private void removeEmptyColumn(List<String> shape) {
        if (shape.isEmpty()) return;
        int maxLen = shape.stream().mapToInt(String::length).max().orElse(0);
        if (maxLen == 0) return;

        boolean[] emptyCol = new boolean[maxLen];
        for (int col = 0; col < maxLen; col++) {
            final int c = col;
            emptyCol[col] = shape.stream().allMatch(s -> c >= s.length() || s.charAt(c) == ' ');
        }

        List<String> newShape = new ArrayList<>();
        for (String row : shape) {
            StringBuilder newRow = new StringBuilder();
            for (int col = 0; col < maxLen; col++) {
                if (!emptyCol[col] && col < row.length()) {
                    newRow.append(row.charAt(col));
                }
            }
            newShape.add(newRow.toString());
        }
        shape.clear();
        shape.addAll(newShape);
    }

    @Override
    protected Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapedCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapedCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }


    /**
     * 确认创建配方按钮
     * 将GUI中存储的物品解析为配方配置并保存、注册
     */
    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(
            VanillaShapedCreatorConfig.CONFIRM_ICON.value()
        );
        return new TranslatableIcon(iconDisplay) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                Map<Integer, ItemStack> storedItems = Objects.requireNonNull(creator).storedItems();

                // 1. 验证结果物品
                ItemStack result = storedItems.get(RESULT_SLOT);
                if (ItemHelper.isAir(result)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 2. 从3x3网格收集材料
                List<String> ingredientIds = new ArrayList<>();
                for (int slot : INGREDIENT_SLOTS) {
                    ItemStack source = storedItems.get(slot);
                    if (ItemHelper.isAir(source)) {
                        ingredientIds.add(null);
                        continue;
                    }
                    String id = resolveIngredientId(source);
                    ingredientIds.add(id);
                }

                // 3. 检查是否所有材料为空
                if (ingredientIds.stream().allMatch(Objects::isNull)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 4. 解析结果物品ID
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 5. 构建形状字符映射
                // 相同的材料ID分配同一个字符 (a, b, c, ...)
                Map<String, Character> idToCharMap = new LinkedHashMap<>();
                char[] shapeChars = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
                char nextChar = 'a';

                for (int i = 0; i < ingredientIds.size(); i++) {
                    String id = ingredientIds.get(i);
                    if (id == null) {
                        continue;
                    }
                    if (!idToCharMap.containsKey(id)) {
                        idToCharMap.put(id, nextChar);
                        nextChar++;
                    }
                    shapeChars[i] = idToCharMap.get(id);
                }

                // 6. 将3x3网格转为形状行列表
                List<String> shape = new ArrayList<>();
                for (int row = 0; row < 3; row++) {
                    shape.add(new String(shapeChars, row * 3, 3));
                }

                // 7. 移除全空行和全空列
                shape.removeIf(s -> s.trim().isEmpty());
                removeEmptyColumn(shape);

                if (shape.isEmpty()) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 8. 构建材料映射: 字符 -> 物品ID字符串
                Map<Character, String> ingredientIdMap = new LinkedHashMap<>();
                for (Map.Entry<String, Character> entry : idToCharMap.entrySet()) {
                    ingredientIdMap.put(entry.getValue(), entry.getKey());
                }

                // 9. 获取配方书分类
                CraftingBookCategory category = CraftingBookCategory.MISC;
                RecipeBookCategoryIcon categoryIcon = (RecipeBookCategoryIcon) VanillaShapedCreator.this.getIcon(40);
                if (categoryIcon != null) {
                    category = categoryIcon.category();
                }

                String recipeName = resolveRecipeName(resultId.itemId());
                // 10. 创建并保存配方配置文件
                BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                recipeConfig.set("type", SimpleRecipeTypes.VANILLA_SHAPED.typeKey());
                recipeConfig.set("result", resultId.toString());
                recipeConfig.set("shape", shape);
                recipeConfig.set("ingredients", ingredientIdMap);
                recipeConfig.set("recipe_book_category", category.name().toLowerCase());
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();

                // 11. 加载配方到RecipeManager
                boolean loadResult = RecipeManager.INSTANCE.loadRecipeFromConfig(recipeName, recipeConfig);
                if (loadResult) {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.COMMAND_CREATE_SUCCESS,
                        Map.of(
                            "<recipe_type>",
                            Languages.RECIPE_TYPE_NAME_VANILLA_SHAPED.value((Player) event.getWhoClicked()),
                            "<recipe_name>",
                            recipeName
                        )
                    );
                } else {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.RECIPE_LOAD_EXCEPTION,
                        Map.of("<recipe_name>", recipeName)
                    );
                }

                // 12. 关闭菜单
                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }

}
