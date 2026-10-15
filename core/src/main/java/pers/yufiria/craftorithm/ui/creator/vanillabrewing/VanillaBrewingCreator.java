package pers.yufiria.craftorithm.ui.creator.vanillabrewing;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.creator.VanillaBrewingCreatorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class VanillaBrewingCreator extends RecipeCreator {

    private static final int INPUT_SLOT = 29;
    private static final int INGREDIENT_SLOT = 11;
    private static final int RESULT_SLOT = 24;

    public VanillaBrewingCreator(@NotNull Player player, @Nullable String recipeId, @Nullable String recipeFileName) {
        super(player, recipeId, recipeFileName);
        this.display = new MenuDisplay(
            VanillaBrewingCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "##G##FFF#",
                "####AFRF#",
                "##I##FFF#",
                "#########"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('A', this::getConfirmIcon);
                layoutMap.put('F', this::getResultFrameIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected RecipeType recipeType() {
        return SimpleRecipeTypes.VANILLA_BREWING;
    }

    @Override
    protected Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaBrewingCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaBrewingCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(
            VanillaBrewingCreatorConfig.CONFIRM_ICON.value()
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

            // 2. 验证输入物品
            ItemStack inputItem = storedItems.get(INPUT_SLOT);
            if (ItemHelper.isAir(inputItem)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 3. 验证材料物品
            ItemStack ingredientItem = storedItems.get(INGREDIENT_SLOT);
            if (ItemHelper.isAir(ingredientItem)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 4. 解析结果物品ID
            NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 5. 解析输入和材料物品ID
            String inputId = resolveIngredientId(inputItem);
            String ingredientId = resolveIngredientId(ingredientItem);
            if (inputId == null || ingredientId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 6. 创建并保存配方配置文件
            saveAndRegister((Player) event.getWhoClicked(), resultId, recipeConfig -> {
                recipeConfig.set("input", inputId);
                recipeConfig.set("ingredient", ingredientId);
            });
        });
        return icon;
    }

}
