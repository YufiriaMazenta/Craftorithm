package pers.yufiria.craftorithm.ui.creator.anvil;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.creator.AnvilCreatorConfig;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.ConversationUtils;
import pers.yufiria.craftorithm.ui.SimpleResultIdResolver;
import pers.yufiria.craftorithm.ui.StackedIngredientIdResolver;
import pers.yufiria.craftorithm.ui.StoredMenuUtils;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.ui.icon.TranslatableIconParser;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AnvilCreator extends RecipeCreator {

    private static final int BASE_SLOT = 11;
    private static final int ADDITION_SLOT = 13;
    private static final int RESULT_SLOT = 15;

    private int costLevel;

    public AnvilCreator(@NotNull Player player, @Nullable String recipeId, @Nullable String recipeFileName) {
        super(
            player,
            recipeId,
            recipeFileName
        );
        this.costLevel = AnvilCreatorConfig.DEFAULT_COST_LEVEL.value();
        this.display = new MenuDisplay(
            AnvilCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "##B#A#RK#",
                "#########",
                "####C####",
                "####L####"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('K', () -> getResultSaveAllComponentsIcon(AnvilCreatorConfig.RESULT_SAVE_ALL_COMPONENTS_ICON.value()));
                layoutMap.put('C', this::getConfirmIcon);
                layoutMap.put('L', this::getCostLevelIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected RecipeType recipeType() {
        return SimpleRecipeTypes.ANVIL;
    }

    private Icon getFrameIcon() {
        return TranslatableIconParser.INSTANCE.parse(AnvilCreatorConfig.FRAME_ICON.value()).get();
    }

    private Icon getResultFrameIcon() {
        return new Icon(new ItemStack(Material.AIR));
    }

    private Icon getCostLevelIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(
            AnvilCreatorConfig.COST_LEVEL_ICON.value()
        );
        return new TranslatableIcon(iconDisplay) {
            @Override
            public ItemStack display() {
                Map<String, String> rm = new HashMap<>();
                rm.put("<level>", String.valueOf(costLevel));
                setTextReplaceMap(rm);
                return super.display();
            }

            @Override
            public Icon onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                // 关闭菜单
                player.closeInventory();
                // 启动对话输入
                ConversationUtils.startNumberInput(
                    player,
                    AnvilCreator.this,
                    Languages.COMMAND_CREATE_INPUT_HINT_ANVIL_COST_LEVEL,
                    input -> costLevel = Math.max(0, input.intValue())
                );
                return this;
            }
        };
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = TranslatableIconParser.INSTANCE.parseIconDisplay(
            AnvilCreatorConfig.CONFIRM_ICON.value()
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

            // 2. 验证base和addition
            ItemStack baseItem = storedItems.get(BASE_SLOT);
            ItemStack additionItem = storedItems.get(ADDITION_SLOT);
            if (ItemHelper.isAir(baseItem) || ItemHelper.isAir(additionItem)) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 3. 解析结果物品ID
            NamespacedItemIdStack resultId = resolveResultId(result);
            if (resultId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                return;
            }

            // 4. 解析材料物品ID
            String baseId = resolveIngredientId(baseItem);
            String additionId = resolveIngredientId(additionItem);
            if (baseId == null || additionId == null) {
                LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                return;
            }

            // 5. 创建并保存配方配置文件
            saveAndRegister((Player) event.getWhoClicked(), resultId, recipeConfig -> {
                recipeConfig.set("base", baseId);
                recipeConfig.set("addition", additionId);
                recipeConfig.set("cost_level", costLevel);
            });
        });
        return icon;
    }

    @Override
    public @Nullable String resolveIngredientId(@Nullable ItemStack item) {
        return StackedIngredientIdResolver.INSTANCE.resolveIngredientId(item);
    }

}
