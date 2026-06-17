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
import pers.yufiria.craftorithm.config.menu.creator.VanillaShapelessCreatorConfig;
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

public class VanillaShapelessCreator extends RecipeCreator {

    /**
     * 3x3合成网格对应的GUI槽位:
     * 行0: 10, 11, 12
     * 行1: 19, 20, 21
     * 行2: 28, 29, 30
     */
    private static final int[] INGREDIENT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int RESULT_SLOT = 24;

    public VanillaShapelessCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
        this.display = new MenuDisplay(
            VanillaShapelessCreatorConfig.TITLE.value(),
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
    protected Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapelessCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaShapelessCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }


    /**
     * 确认创建配方按钮
     * 将GUI中存储的物品解析为配方配置并保存、注册
     */
    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(
            VanillaShapelessCreatorConfig.CONFIRM_ICON.value()
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
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                // 4. 解析结果物品ID
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 5. 获取配方书分类
                CraftingBookCategory category = CraftingBookCategory.MISC;
                RecipeBookCategoryIcon categoryIcon = (RecipeBookCategoryIcon) VanillaShapelessCreator.this.getIcon(40);
                if (categoryIcon != null) {
                    category = categoryIcon.category();
                }

                String recipeName = resolveRecipeName(resultId.itemId());
                // 6. 创建并保存配方配置文件
                BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                recipeConfig.set("type", SimpleRecipeTypes.VANILLA_SHAPELESS.typeKey());
                recipeConfig.set("result", resultId.toString());
                recipeConfig.set("ingredients", ingredientIds);
                recipeConfig.set("recipe_book_category", category.name().toLowerCase());
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();

                // 7. 加载配方到RecipeManager
                boolean loadResult = RecipeManager.INSTANCE.loadRecipeFromConfig(recipeName, recipeConfig);
                if (loadResult) {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.COMMAND_CREATE_SUCCESS,
                        Map.of(
                            "<recipe_type>",
                            Languages.RECIPE_TYPE_NAME_VANILLA_SHAPELESS.value((Player) event.getWhoClicked()),
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

                // 8. 关闭菜单
                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }

}
