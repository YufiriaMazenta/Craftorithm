package pers.yufiria.craftorithm.ui.creator.vanillaBrewing;

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
import pers.yufiria.craftorithm.config.menu.creator.VanillaBrewingCreatorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
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

public class VanillaBrewingCreator extends RecipeCreator {

    private static final int INPUT_SLOT = 11;
    private static final int INGREDIENT_SLOT = 29;
    private static final int RESULT_SLOT = 24;

    public VanillaBrewingCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
        this.display = new MenuDisplay(
            VanillaBrewingCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "##I##FFF#",
                "####AFRF#",
                "##G##FFF#",
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
    protected Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaBrewingCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(VanillaBrewingCreatorConfig.RESULT_FRAME_ICON.value()).get();
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(
            VanillaBrewingCreatorConfig.CONFIRM_ICON.value()
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

                // 2. 验证输入物品
                ItemStack inputItem = storedItems.get(INPUT_SLOT);
                if (ItemHelper.isAir(inputItem)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                // 3. 验证材料物品
                ItemStack ingredientItem = storedItems.get(INGREDIENT_SLOT);
                if (ItemHelper.isAir(ingredientItem)) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                // 4. 解析结果物品ID
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                // 5. 解析输入和材料物品ID
                String inputId = resolveIngredientId(inputItem);
                String ingredientId = resolveIngredientId(ingredientItem);

                if (inputId == null || ingredientId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                String recipeName = resolveRecipeName(resultId.itemId());
                // 6. 创建并保存配方配置文件
                BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                recipeConfig.set("type", SimpleRecipeTypes.VANILLA_BREWING.typeKey());
                recipeConfig.set("result", resultId.toString());
                recipeConfig.set("input", inputId);
                recipeConfig.set("ingredient", ingredientId);
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();

                // 7. 加载配方到RecipeManager
                boolean loadResult = RecipeManager.INSTANCE.loadRecipeFromConfig(recipeName, recipeConfig, false);
                if (loadResult) {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.COMMAND_CREATE_SUCCESS,
                        Map.of(
                            "<recipe_type>",
                            Languages.RECIPE_TYPE_NAME_VANILLA_BREWING.value((Player) event.getWhoClicked()),
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
