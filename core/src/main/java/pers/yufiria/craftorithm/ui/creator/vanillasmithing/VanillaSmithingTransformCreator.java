package pers.yufiria.craftorithm.ui.creator.vanillasmithing;

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
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmithingTransformCreatorConfig;
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

public class VanillaSmithingTransformCreator extends RecipeCreator {

    private static final int TEMPLATE_SLOT = 19;
    private static final int BASE_SLOT = 21;
    private static final int ADDITION_SLOT = 23;
    private static final int RESULT_SLOT = 25;

    public VanillaSmithingTransformCreator(@NotNull Player player, @Nullable String recipeId, @Nullable String recipeFileName) {
        super(player, recipeId, recipeFileName);
        this.display = new MenuDisplay(
            VanillaSmithingTransformCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "######FFF",
                "#T#BCAFRF",
                "######FFF",
                "#########"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('C', this::getConfirmIcon);
                layoutMap.put('F', this::getResultFrameIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected RecipeType recipeType() {
        return SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM;
    }

    @Override
    protected Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaSmithingTransformCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(VanillaSmithingTransformCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(
            VanillaSmithingTransformCreatorConfig.CONFIRM_ICON.value()
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

            // 2. 验证三个输入槽位
            ItemStack templateItem = storedItems.get(TEMPLATE_SLOT);
            ItemStack baseItem = storedItems.get(BASE_SLOT);
            ItemStack additionItem = storedItems.get(ADDITION_SLOT);
            if (ItemHelper.isAir(templateItem) || ItemHelper.isAir(baseItem) || ItemHelper.isAir(additionItem)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 3. 解析结果物品ID
            NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 4. 解析材料物品ID
            String templateId = resolveIngredientId(templateItem);
            String baseId = resolveIngredientId(baseItem);
            String additionId = resolveIngredientId(additionItem);
            if (templateId == null || baseId == null || additionId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 5. 创建并保存配方配置文件
            saveAndRegister((Player) event.getWhoClicked(), resultId, recipeConfig -> {
                recipeConfig.set("template", templateId);
                recipeConfig.set("base", baseId);
                recipeConfig.set("addition", additionId);
            });
        });
        return icon;
    }

}
