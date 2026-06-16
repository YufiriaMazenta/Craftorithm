package pers.yufiria.craftorithm.ui.creator.anvil;

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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.creator.AnvilCreatorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.Supplier;

public class AnvilCreator extends RecipeCreator {

    private static final int BASE_SLOT = 10;
    private static final int ADDITION_SLOT = 12;
    private static final int RESULT_SLOT = 16;

    private int costLevel;

    public AnvilCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
        this.costLevel = AnvilCreatorConfig.DEFAULT_COST_LEVEL.value();
        this.display = new MenuDisplay(
            AnvilCreatorConfig.TITLE.value(),
            new MenuLayout(Arrays.asList(
                "#########",
                "#B#A###R#",
                "##L###C##",
                "#########"
            ), () -> {
                Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                layoutMap.put('#', this::getFrameIcon);
                layoutMap.put('C', this::getConfirmIcon);
                layoutMap.put('L', this::getCostLevelIcon);
                return layoutMap;
            })
        );
    }

    @Override
    protected Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(AnvilCreatorConfig.FRAME_ICON.value()).get();
    }

    @Override
    protected Icon getResultFrameIcon() {
        return new Icon(new ItemStack(Material.AIR));
    }

    private Icon getCostLevelIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(
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
                new Conversation(Craftorithm.instance(), player,
                    new NumberPrompt() {
                        @Override
                        public Prompt acceptValidatedInput(Map<Object, Object> conversationData, Number input) {
                            costLevel = Math.max(0, input.intValue());
                            return null; // 结束对话
                        }

                        @Override
                        public BaseComponent promptText(Map<Object, Object> conversationData) {
                            return BukkitTextProcessor.toComponent(
                                BukkitTextProcessor.color(
                                    Languages.COMMAND_CREATE_INPUT_HINT_ANVIL_COST_LEVEL.value(player)
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
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(
            AnvilCreatorConfig.CONFIRM_ICON.value()
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

                // 2. 验证base和addition
                ItemStack baseItem = storedItems.get(BASE_SLOT);
                ItemStack additionItem = storedItems.get(ADDITION_SLOT);

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
                String baseId = resolveIngredientId(baseItem);
                String additionId = resolveIngredientId(additionItem);

                if (baseId == null || additionId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_INGREDIENTS);
                    return this;
                }

                String recipeName = resolveRecipeName(resultId.itemId());
                // 5. 创建并保存配方配置文件
                BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                recipeConfig.set("type", SimpleRecipeTypes.ANVIL.typeKey());
                recipeConfig.set("result", resultId.toString());
                recipeConfig.set("base", baseId);
                recipeConfig.set("addition", additionId);
                recipeConfig.set("cost_level", costLevel);
                recipeConfig.saveConfig();
                recipeConfig.reloadConfig();

                // 6. 加载配方到RecipeManager
                boolean loadResult = RecipeManager.INSTANCE.loadRecipeFromConfig(recipeName, recipeConfig);
                if (loadResult) {
                    LangUtils.sendLang(
                        event.getWhoClicked(),
                        Languages.COMMAND_CREATE_SUCCESS,
                        Map.of(
                            "<recipe_type>",
                            Languages.RECIPE_TYPE_NAME_ANVIL.value((Player) event.getWhoClicked()),
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

    @Override
    protected String resolveIngredientId(ItemStack item) {
        NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemIdOrCreate(item, false);
        return itemId != null ? itemId.toString() : null;
    }
}
