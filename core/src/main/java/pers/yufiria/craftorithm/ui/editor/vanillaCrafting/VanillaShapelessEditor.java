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
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaShapelessEditorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.vanillaCrafting.RecipeBookCategoryIcon;
import pers.yufiria.craftorithm.ui.editor.EditorIconParser;
import pers.yufiria.craftorithm.ui.editor.RecipeEditorMenu;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * 无序合成配方编辑器
 * 材料槽位: 10,11,12 / 19,20,21 / 28,29,30 (3x3网格)
 * 结果槽位: 24
 */
public final class VanillaShapelessEditor extends RecipeEditorMenu {

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
                "B########",
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
        return EditorIconParser.INSTANCE.parse(VanillaShapelessEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return EditorIconParser.INSTANCE.parse(VanillaShapelessEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(VanillaShapelessEditorConfig.BACK_ICON.value());
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = EditorIconParser.INSTANCE.parseIconDisplay(VanillaShapelessEditorConfig.CONFIRM_ICON.value());
        return new TranslatableIcon(iconDisplay) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                Map<Integer, ItemStack> storedItems = Objects.requireNonNull(creator).storedItems();

                ItemStack result = storedItems.get(RESULT_SLOT);
                if (ItemHelper.isAir(result)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
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
                    return this;
                }

                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
                if (configWrapper != null) {
                    configWrapper.set("type", SimpleRecipeTypes.VANILLA_SHAPELESS.typeKey());
                    configWrapper.set("result", resultId.toString());
                    configWrapper.set("ingredients", ingredientIds);
                    configWrapper.set("recipe_book_category", categoryIcon.category().name().toLowerCase());
                    saveRecipeEdit(configWrapper, () -> {
                        String fileName = RecipeManager.INSTANCE.getRecipeFileNameByKey(recipeKey);
                        LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_EDIT_SUCCESS, Map.of(
                            "<recipe_id>", recipeId,
                            "<recipe_file_name>", fileName != null ? fileName : recipeId
                        ));
                    });
                }

                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }
}
