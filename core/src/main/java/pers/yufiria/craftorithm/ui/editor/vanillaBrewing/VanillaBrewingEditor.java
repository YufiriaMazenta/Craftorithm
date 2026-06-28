package pers.yufiria.craftorithm.ui.editor.vanillaBrewing;

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
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaBrewingEditorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.ui.editor.EditorIconParser;
import pers.yufiria.craftorithm.ui.editor.RecipeEditorMenu;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 酿造台配方编辑器
 * 布局与VanillaBrewingCreator一致。
 *
 * 槽位（与Creator一致）:
 *   INPUT(被修改的药水): 11
 *   INGREDIENT(修改材料): 29
 *   RESULT(结果): 24
 *
 * 布局:
 *   #########
 *   ##I##FFF#
 *   ####AFRF#
 *   ##G##FFF#
 *   #########
 */
public final class VanillaBrewingEditor extends RecipeEditorMenu {

    private static final int INPUT_SLOT = 11;
    private static final int INGREDIENT_SLOT = 29;
    private static final int RESULT_SLOT = 24;

    private final BrewingRecipe brewingRecipe;

    public VanillaBrewingEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull BrewingRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.brewingRecipe = recipe;
        this.display = new MenuDisplay(
            VanillaBrewingEditorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "B########",
                "##I##FFF#",
                "####AFRF#",
                "##G##FFF#",
                "#########"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('A', this::getConfirmIcon);
                layoutMap.put('F', this::getResultFrameIcon);
                layoutMap.put('B', this::getBackIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected void fillRecipeData(Inventory inventory) {
        // input - RecipeChoice, 用getItemStack()获取显示物品
        inventory.setItem(INPUT_SLOT, brewingRecipe.input().getItemStack().clone());

        // ingredient - RecipeChoice
        inventory.setItem(INGREDIENT_SLOT, brewingRecipe.ingredient().getItemStack().clone());

        // result - ItemStack
        ItemStack result = brewingRecipe.getResult();
        inventory.setItem(RESULT_SLOT, result.clone());
    }

    private Icon getFrameIcon() {
        return EditorIconParser.INSTANCE.parse(VanillaBrewingEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return EditorIconParser.INSTANCE.parse(VanillaBrewingEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(VanillaBrewingEditorConfig.BACK_ICON.value());
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = EditorIconParser.INSTANCE.parseIconDisplay(VanillaBrewingEditorConfig.CONFIRM_ICON.value());
        return new TranslatableIcon(iconDisplay) {
            @Override
            public Icon onClick(InventoryClickEvent event) {
                StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                Map<Integer, ItemStack> storedItems = Objects.requireNonNull(creator).storedItems();

                // 验证三个槽位
                ItemStack result = storedItems.get(RESULT_SLOT);
                if (ItemHelper.isAir(result)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                ItemStack inputItem = storedItems.get(INPUT_SLOT);
                if (ItemHelper.isAir(inputItem)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                ItemStack ingredientItem = storedItems.get(INGREDIENT_SLOT);
                if (ItemHelper.isAir(ingredientItem)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                String inputId = resolveIngredientId(inputItem);
                String ingredientId = resolveIngredientId(ingredientItem);

                BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
                if (configWrapper != null) {
                    configWrapper.set("type", SimpleRecipeTypes.VANILLA_BREWING.typeKey());
                    configWrapper.set("result", resultId.toString());
                    if (inputId != null) configWrapper.set("input", inputId);
                    if (ingredientId != null) configWrapper.set("ingredient", ingredientId);
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
