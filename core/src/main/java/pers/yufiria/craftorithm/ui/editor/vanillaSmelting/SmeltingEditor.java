package pers.yufiria.craftorithm.ui.editor.vanillaSmelting;

import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.SmeltingMenuType;
import pers.yufiria.craftorithm.ui.creator.vanillaSmelting.CookingRecipeBookCategoryIcon;
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
 * 烧炼配方编辑器
 * 熔炉/高炉/烟熏炉/营火共用此类, 差异部分由SmeltingMenuType提供
 * 布局与SmeltingCreator一致，预填充现有配方数据。
 * 使用Conversation修改exp/time，与Creator一致。
 *
 * 材料槽位: 20
 * 结果槽位: 24
 * exp/time通过图标点击触发Conversation修改
 */
public class SmeltingEditor extends RecipeEditorMenu {

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
                "B########",
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
        return EditorIconParser.INSTANCE.parse(smeltingType.editorConfigs().frameIcon().value()).get();
    }

    private Icon getResultFrameIcon() {
        return EditorIconParser.INSTANCE.parse(smeltingType.editorConfigs().resultFrameIcon().value()).get();
    }

    private Icon getBackIcon() {
        return createBackIcon(smeltingType.editorConfigs().backIcon().value());
    }

    private Icon getExpIcon() {
        IconDisplay iconDisplay = EditorIconParser.INSTANCE.parseIconDisplay(smeltingType.editorConfigs().expIcon().value());
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
                new Conversation(Craftorithm.instance(), player,
                    new NumberPrompt() {
                        @Override
                        public Prompt acceptValidatedInput(Map<Object, Object> conversationData, Number input) {
                            exp = input.floatValue();
                            return null;
                        }

                        @Override
                        public BaseComponent promptText(Map<Object, Object> conversationData) {
                            return BukkitTextProcessor.toComponent(
                                BukkitTextProcessor.color(
                                    Languages.COMMAND_CREATE_INPUT_HINT_SMELTING_EXP.value(player)
                                )
                            );
                        }
                    },
                    data -> {
                        updateMenu();
                        CrypticLibBukkit.scheduler().sync(() -> openMenu());
                    }
                ).start();
                return this;
            }
        };
    }

    private Icon getTimeIcon() {
        IconDisplay iconDisplay = EditorIconParser.INSTANCE.parseIconDisplay(smeltingType.editorConfigs().timeIcon().value());
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
                new Conversation(Craftorithm.instance(), player,
                    new NumberPrompt() {
                        @Override
                        public Prompt acceptValidatedInput(Map<Object, Object> conversationData, Number input) {
                            time = Math.max(1, input.intValue());
                            return null;
                        }

                        @Override
                        public BaseComponent promptText(Map<Object, Object> conversationData) {
                            return BukkitTextProcessor.toComponent(
                                BukkitTextProcessor.color(
                                    Languages.COMMAND_CREATE_INPUT_HINT_SMELTING_TIME.value(player)
                                )
                            );
                        }
                    },
                    data -> {
                        updateMenu();
                        CrypticLibBukkit.scheduler().sync(() -> openMenu());
                    }
                ).start();
                return this;
            }
        };
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = EditorIconParser.INSTANCE.parseIconDisplay(smeltingType.editorConfigs().confirmIcon().value());
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

                String ingredientId = resolveIngredientId(ingredientItem);
                if (ingredientId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
                if (configWrapper != null) {
                    // 保持原有的type不变
                    configWrapper.set("result", resultId.toString());
                    configWrapper.set("ingredient", ingredientId);
                    configWrapper.set("exp", exp);
                    configWrapper.set("time", time);
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
