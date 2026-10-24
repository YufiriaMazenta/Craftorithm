package pers.yufiria.craftorithm.ui.editor.vanillasmelting;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.ui.ConversationUtils;
import pers.yufiria.craftorithm.ui.SmeltingMenuType;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.creator.vanillasmelting.CookingRecipeBookCategoryIcon;
import pers.yufiria.craftorithm.ui.editor.RecipeEditor;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 烧炼配方编辑器
 * 熔炉/高炉/烟熏炉/营火共用此类, 差异部分由SmeltingMenuType提供
 * 布局与SmeltingCreator一致，预填充现有配方数据。
 * 使用Conversation修改exp/time，与Creator一致。
 *
 * 材料槽位: 20
 * 结果槽位: 24
 * exp/time通过图标点击触发Conversation修改
 */
public class SmeltingEditor extends RecipeEditor {

    private static final int INGREDIENT_SLOT = 20;
    private static final int RESULT_SLOT = 24;

    protected final SmeltingMenuType smeltingType;
    protected final CookingRecipe<?> cookingRecipe;
    protected float exp;
    protected int time;
    protected final CookingRecipeBookCategoryIcon categoryIcon;

    public SmeltingEditor(
        @NotNull Player player,
        @NotNull NamespacedKey recipeKey,
        @NotNull CookingRecipe<?> recipe,
        @NotNull SmeltingMenuType smeltingType
    ) {
        super(player, recipeKey, recipeKey.toString());
        this.smeltingType = smeltingType;
        this.cookingRecipe = recipe;
        this.exp = recipe.getExperience();
        this.time = recipe.getCookingTime();
        SmeltingMenuType.EditorConfigs configs = smeltingType.editorConfigs();
        this.categoryIcon = new CookingRecipeBookCategoryIcon(
            configs.categoryIconFood(), configs.categoryIconBlocks(), configs.categoryIconMisc()
        );
        this.display = new MenuDisplay(
            configs.title().value(),
            new MenuLayout(Arrays.asList(
                "B#######D",
                "#####FFF#",
                "##I#AFRF#",
                "#####FFF#",
                "##E#G#T##"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('A', this::getConfirmIcon);
                layoutMap.put('E', this::getExpIcon);
                layoutMap.put('T', this::getTimeIcon);
                layoutMap.put('F', this::getResultFrameIcon);
                layoutMap.put('B', this::getBackIcon);
                layoutMap.put('G', () -> categoryIcon);
                layoutMap.put('D', this::getDeleteIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected void fillRecipeData(Inventory inventory) {
        // 填充材料
        ItemStack source = cookingRecipe.getInput();
        inventory.setItem(INGREDIENT_SLOT, source.clone());
        // 填充结果
        ItemStack result = cookingRecipe.getResult();
        inventory.setItem(RESULT_SLOT, result.clone());

        categoryIcon.setCategory(cookingRecipe.getCategory());
    }

    private Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(smeltingType.editorConfigs().frameIcon().value()).get();
    }

    private Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(smeltingType.editorConfigs().resultFrameIcon().value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(smeltingType.editorConfigs().backIcon().value());
    }

    private Icon getDeleteIcon() {
        return createDeleteIcon(smeltingType.editorConfigs().deleteIcon().value());
    }

    private Icon getExpIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(smeltingType.editorConfigs().expIcon().value());
        return new TranslatableIcon(iconDisplay) {
            @Override
            public ItemStack display() {
                Map<String, String> rm = new HashMap<>();
                rm.put("<exp>", String.valueOf(exp));
                setTextReplaceMap(rm);
                return super.display();
            }

            @Override
            public Icon onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                ConversationUtils.startNumberInput(
                    player,
                    SmeltingEditor.this,
                    Languages.COMMAND_CREATE_INPUT_HINT_SMELTING_EXP,
                    input -> exp = input.floatValue()
                );
                return this;
            }
        };
    }

    private Icon getTimeIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(smeltingType.editorConfigs().timeIcon().value());
        return new TranslatableIcon(iconDisplay) {
            @Override
            public ItemStack display() {
                Map<String, String> rm = new HashMap<>();
                rm.put("<time>", String.valueOf(time));
                setTextReplaceMap(rm);
                return super.display();
            }

            @Override
            public Icon onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                ConversationUtils.startNumberInput(
                    player,
                    SmeltingEditor.this,
                    Languages.COMMAND_CREATE_INPUT_HINT_SMELTING_TIME,
                    input -> time = Math.max(1, input.intValue())
                );
                return this;
            }
        };
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(smeltingType.editorConfigs().confirmIcon().value());
        TranslatableIcon icon = new TranslatableIcon(iconDisplay);
        icon.setClickAction(event -> {
            Map<Integer, ItemStack> storedItems = StoredMenuUtils.storedItems(event);

            ItemStack result = storedItems.get(RESULT_SLOT);
            if (ItemHelper.isAir(result)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            ItemStack ingredientItem = storedItems.get(INGREDIENT_SLOT);
            if (ItemHelper.isAir(ingredientItem)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            NamespacedItemIdStack resultId = resolveResultId(result);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            String ingredientId = resolveIngredientId(ingredientItem);
            if (ingredientId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 保持原有的type不变
            saveEdit((Player) event.getWhoClicked(), configWrapper -> {
                configWrapper.set("result", resultId.toString());
                configWrapper.set("ingredient", ingredientId);
                configWrapper.set("exp", exp);
                configWrapper.set("time", time);
                configWrapper.set("recipe_book_category", categoryIcon.category().name().toLowerCase());
            });
        });
        return icon;
    }

}
