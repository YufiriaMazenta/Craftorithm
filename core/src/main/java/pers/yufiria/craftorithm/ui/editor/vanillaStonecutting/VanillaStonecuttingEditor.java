package pers.yufiria.craftorithm.ui.editor.vanillaStonecutting;

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
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaStonecuttingEditorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.editor.RecipeEditorMenu;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 切石机配方编辑器
 * 材料: 20, 结果: 24
 */
public final class VanillaStonecuttingEditor extends RecipeEditorMenu {

    private static final int INGREDIENT_SLOT = 20;
    private static final int RESULT_SLOT = 25;

    private final StonecuttingRecipe stonecuttingRecipe;

    public VanillaStonecuttingEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull StonecuttingRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.stonecuttingRecipe = recipe;
        this.display = new MenuDisplay(
            VanillaStonecuttingEditorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "#####FFF#",
                "##I#AFRF#",
                "#####FFF#",
                "#########",
                "#########"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('F', this::getResultFrameIcon);
                layoutMap.put('A', this::getConfirmIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected void fillRecipeData(Inventory inventory) {
        ItemStack source = stonecuttingRecipe.getInput();
        inventory.setItem(INGREDIENT_SLOT, source.clone());
        ItemStack result = stonecuttingRecipe.getResult();
        inventory.setItem(RESULT_SLOT, result.clone());
    }

    private Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaStonecuttingEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaStonecuttingEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(VanillaStonecuttingEditorConfig.CONFIRM_ICON.value());
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

                String ingredientId = resolveIngredientId(storedItems.get(INGREDIENT_SLOT));
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
                if (configWrapper != null) {
                    configWrapper.set("type", SimpleRecipeTypes.VANILLA_STONECUTTING.typeKey());
                    if (ingredientId != null) configWrapper.set("ingredient", ingredientId);
                    configWrapper.set("result", resultId.toString());
                    saveRecipeConfig(configWrapper);
                }

                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }
}
