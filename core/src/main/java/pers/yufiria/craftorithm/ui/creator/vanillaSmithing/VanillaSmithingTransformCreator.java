package pers.yufiria.craftorithm.ui.creator.vanillaSmithing;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmithingTransformCreatorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        return CreatorIconParser.INSTANCE.parse(VanillaSmithingTransformCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaSmithingTransformCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(
            VanillaSmithingTransformCreatorConfig.CONFIRM_ICON.value()
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

                // 2. 验证三个输入槽位
                ItemStack templateItem = storedItems.get(TEMPLATE_SLOT);
                ItemStack baseItem = storedItems.get(BASE_SLOT);
                ItemStack additionItem = storedItems.get(ADDITION_SLOT);

                if (ItemHelper.isAir(templateItem)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }
                if (ItemHelper.isAir(baseItem)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }
                if (ItemHelper.isAir(additionItem)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                // 3. 解析结果物品ID
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 4. 解析材料物品ID
                String templateId = resolveIngredientId(templateItem);
                String baseId = resolveIngredientId(baseItem);
                String additionId = resolveIngredientId(additionItem);

                if (templateId == null || baseId == null || additionId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                String recipeId = resolveRecipeId(SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM.typeKey(), resultId.itemId());
                String recipeFileName = resolveRecipeFileName(resultId.itemId());
                // 5. 创建并保存配方配置文件
                BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeFileName);
                recipeConfig.set("type", SimpleRecipeTypes.VANILLA_SMITHING_TRANSFORM.typeKey());
                recipeConfig.set("result", resultId.toString());
                recipeConfig.set("template", templateId);
                recipeConfig.set("base", baseId);
                recipeConfig.set("addition", additionId);
                if (recipeId != null) {
                    recipeConfig.set("recipe_id", recipeId);
                }
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();

                // 6. 加载配方到RecipeManager
                boolean loadResult = RecipeManager.INSTANCE.loadRecipeFromConfig(recipeFileName, recipeConfig);
                if (loadResult) {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.COMMAND_CREATE_SUCCESS,
                        Map.of(
                            "<recipe_type>",
                            Languages.RECIPE_TYPE_NAME_VANILLA_SMITHING_TRANSFORM.value((Player) event.getWhoClicked()),
                            "<recipe_file_name>",
                            recipeFileName,
                            "<recipe_id>",
                            recipeId != null ? recipeId : recipeFileName
                        )
                    );
                } else {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.RECIPE_LOAD_EXCEPTION,
                        Map.of("<recipe_name>", recipeFileName)
                    );
                }

                // 7. 关闭菜单
                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }

}
