package pers.yufiria.craftorithm.ui.creator.vanillaSmelting;

import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
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

/**
 * 熔炼配方创建器基类
 * 提供熔炉/高炉/烟熏炉/营火配方创建的通用逻辑
 */
public abstract class AbstractSmeltingCreator extends RecipeCreator {

    private static final int INGREDIENT_SLOT = 20;
    private static final int RESULT_SLOT = 24;

    protected float exp;
    protected int time;
    protected CookingRecipeBookCategoryIcon categoryIcon;

    public AbstractSmeltingCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
        this.exp = defaultExp();
        this.time = defaultTime();
        this.categoryIcon = new CookingRecipeBookCategoryIcon(
            categoryIconFoodConfig(), categoryIconBlocksConfig(), categoryIconMiscConfig()
        );
        this.display = new MenuDisplay(
            title().value(),
            new MenuLayout(Arrays.asList(
                "#########",
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
                layoutMap.put('G', () -> categoryIcon);
                return layoutMap;
            })
        );
    }

    // ---- 抽象方法：子类提供配置 ----
    protected abstract StringConfig title();
    protected abstract ConfigSectionConfig frameIconConfig();
    protected abstract ConfigSectionConfig resultFrameIconConfig();
    protected abstract ConfigSectionConfig confirmIconConfig();
    protected abstract ConfigSectionConfig expIconConfig();
    protected abstract ConfigSectionConfig timeIconConfig();
    protected abstract ConfigSectionConfig categoryIconFoodConfig();
    protected abstract ConfigSectionConfig categoryIconBlocksConfig();
    protected abstract ConfigSectionConfig categoryIconMiscConfig();
    protected abstract int defaultExp();
    protected abstract int defaultTime();
    protected abstract SimpleRecipeTypes recipeType();

    @Override
    protected Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(frameIconConfig().value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(resultFrameIconConfig().value()).get();
    }

    private Icon getExpIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(expIconConfig().value());
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
                            return null; // 结束对话
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
                        // 对话结束后重新打开菜单
                        updateMenu();
                        CrypticLibBukkit.scheduler().sync(() -> openMenu());
                    }
                ).start();
                return this;
            }
        };
    }

    private Icon getTimeIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(timeIconConfig().value());
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
                            return null; // 结束对话
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
                        // 对话结束后重新打开菜单
                        updateMenu();
                        CrypticLibBukkit.scheduler().sync(() -> openMenu());
                    }
                ).start();
                return this;
            }
        };
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(confirmIconConfig().value());
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

                // 2. 验证材料物品
                ItemStack ingredientItem = storedItems.get(INGREDIENT_SLOT);
                if (ItemHelper.isAir(ingredientItem)) {
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
                String ingredientId = resolveIngredientId(ingredientItem);
                if (ingredientId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                String recipeName = resolveRecipeName(resultId.itemId());
                // 5. 创建并保存配方配置文件
                BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                recipeConfig.set("type", recipeType().typeKey());
                recipeConfig.set("result", resultId.toString());
                recipeConfig.set("ingredient", ingredientId);
                recipeConfig.set("exp", exp);
                recipeConfig.set("time", time);
                recipeConfig.set("recipe_book_category", categoryIcon.category().name().toLowerCase());
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();

                // 6. 加载配方到RecipeManager
                boolean loadResult = RecipeManager.INSTANCE.loadRecipeFromConfig(recipeName, recipeConfig, true);
                if (loadResult) {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.COMMAND_CREATE_SUCCESS,
                        Map.of(
                            "<recipe_type>",
                            recipeTypeName((Player) event.getWhoClicked()),
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

                // 7. 关闭菜单
                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }

    private String recipeTypeName(Player player) {
        return switch (recipeType()) {
            case VANILLA_SMELTING_FURNACE -> Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_FURNACE.value(player);
            case VANILLA_SMELTING_BLAST -> Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_BLAST.value(player);
            case VANILLA_SMELTING_SMOKER -> Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_SMOKER.value(player);
            case VANILLA_SMELTING_CAMPFIRE -> Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_CAMPFIRE.value(player);
            default -> recipeType().typeKey();
        };
    }

}
