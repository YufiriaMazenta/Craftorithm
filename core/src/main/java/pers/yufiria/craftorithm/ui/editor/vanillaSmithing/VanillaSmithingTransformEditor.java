package pers.yufiria.craftorithm.ui.editor.vanillaSmithing;

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
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmithingTransformEditorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
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
 * 锻造台配方编辑器
 * base(模板): 19, addition(添加物): 21, result: 24
 */
public final class VanillaSmithingTransformEditor extends RecipeEditorMenu {

    private static final int TEMPLATE_SLOT = 19;
    private static final int BASE_SLOT = 21;
    private static final int ADDITION_SLOT = 23;
    private static final int RESULT_SLOT = 25;

    private final SmithingRecipe smithingRecipe;

    public VanillaSmithingTransformEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull SmithingRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.smithingRecipe = recipe;
        this.display = new MenuDisplay(
            VanillaSmithingTransformEditorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "X########",
                "######FFF",
                "#T#BCAFRF",
                "######FFF",
                "#########"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('F', this::getResultFrameIcon);
                layoutMap.put('C', this::getConfirmIcon);
                layoutMap.put('X', this::getBackIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected void fillRecipeData(Inventory inventory) {
        var base = smithingRecipe.getBase();
        inventory.setItem(BASE_SLOT, base.getItemStack().clone());

        var addition = smithingRecipe.getAddition();
        inventory.setItem(ADDITION_SLOT, addition.getItemStack().clone());

        var template = ((SmithingTransformRecipe) smithingRecipe).getTemplate();
        inventory.setItem(TEMPLATE_SLOT, template.getItemStack().clone());

        ItemStack result = smithingRecipe.getResult();
        inventory.setItem(RESULT_SLOT, result.clone());
    }

    private Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaSmithingTransformEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaSmithingTransformEditorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(VanillaSmithingTransformEditorConfig.BACK_ICON.value());
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(VanillaSmithingTransformEditorConfig.CONFIRM_ICON.value());
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

                String baseId = resolveIngredientId(storedItems.get(BASE_SLOT));
                String additionId = resolveIngredientId(storedItems.get(ADDITION_SLOT));
                String templateId = resolveIngredientId(storedItems.get(TEMPLATE_SLOT));
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
                if (configWrapper != null) {
                    configWrapper.set("type", SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM.typeKey());
                    if (baseId != null) configWrapper.set("base", baseId);
                    if (additionId != null) configWrapper.set("addition", additionId);
                    if (templateId != null) configWrapper.set("template", templateId);

                    configWrapper.set("result", resultId.toString());
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
