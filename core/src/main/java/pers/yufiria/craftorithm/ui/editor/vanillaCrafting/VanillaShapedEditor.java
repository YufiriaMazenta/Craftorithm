package pers.yufiria.craftorithm.ui.editor.vanillaCrafting;

import crypticlib.config.BukkitConfigWrapper;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaShapedEditorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.editor.RecipeEditorMenu;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

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
public final class VanillaShapedEditor extends RecipeEditorMenu {

    private static final int[] INGREDIENT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int RESULT_SLOT = 24;

    private final ShapedRecipe shapedRecipe;

    public VanillaShapedEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull ShapedRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.shapedRecipe = recipe;
        this.display = new MenuDisplay(
            VanillaShapedEditorConfig.TITLE.value(),
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
    }

    private Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapedEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapedEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(VanillaShapedEditorConfig.CONFIRM_ICON.value());
        return new TranslatableIcon(iconDisplay) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                Map<Integer, ItemStack> storedItems = Objects.requireNonNull(creator).storedItems();

                // 验证结果物品
                ItemStack result = storedItems.get(RESULT_SLOT);
                if (ItemHelper.isAir(result)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 从3x3网格收集材料
                List<String> ingredientIds = new ArrayList<>();
                for (int slot : INGREDIENT_SLOTS) {
                    ItemStack source = storedItems.get(slot);
                    if (ItemHelper.isAir(source)) {
                        ingredientIds.add(null);
                    } else {
                        ingredientIds.add(resolveIngredientId(source));
                    }
                }

                // 验证材料不全为空
                if (ingredientIds.stream().allMatch(Objects::isNull)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 解析结果物品ID
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 构建形状字符映射
                Map<String, Character> idToCharMap = new LinkedHashMap<>();
                char[] shapeChars = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
                char nextChar = 'a';

                for (int i = 0; i < ingredientIds.size(); i++) {
                    String id = ingredientIds.get(i);
                    if (id == null) continue;
                    if (!idToCharMap.containsKey(id)) {
                        idToCharMap.put(id, nextChar);
                        nextChar++;
                    }
                    shapeChars[i] = idToCharMap.get(id);
                }

                // 构建形状行列表
                List<String> shape = new ArrayList<>();
                for (int row = 0; row < 3; row++) {
                    shape.add(new String(shapeChars, row * 3, 3));
                }
                shape.removeIf(s -> s.trim().isEmpty());
                removeEmptyColumn(shape);

                if (shape.isEmpty()) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 构建材料映射
                Map<Character, String> ingredientIdMap = new LinkedHashMap<>();
                for (Map.Entry<String, Character> entry : idToCharMap.entrySet()) {
                    ingredientIdMap.put(entry.getValue(), entry.getKey());
                }

                // 保存配置
                BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
                if (configWrapper != null) {
                    configWrapper.set("type", SimpleRecipeTypes.VANILLA_SHAPED.typeKey());
                    configWrapper.set("result", resultId.toString());
                    configWrapper.set("shape", shape);
                    configWrapper.set("ingredients", ingredientIdMap);
                    saveRecipeConfig(configWrapper);
                }

                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }

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

}
