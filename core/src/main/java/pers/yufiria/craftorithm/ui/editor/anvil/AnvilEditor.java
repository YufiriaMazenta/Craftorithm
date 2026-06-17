package pers.yufiria.craftorithm.ui.editor.anvil;

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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.editor.AnvilEditorConfig;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.editor.RecipeEditorMenu;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * 铁砧配方编辑器
 * 布局与AnvilCreator一致。
 *
 * 槽位（与Creator一致）:
 *   BASE: 10, ADDITION: 12, RESULT: 16
 *   costLevel通过Conversation修改
 */
public final class AnvilEditor extends RecipeEditorMenu {

    private static final int BASE_SLOT = 10;
    private static final int ADDITION_SLOT = 12;
    private static final int RESULT_SLOT = 16;

    private final AnvilRecipe anvilRecipe;
    private int costLevel;

    public AnvilEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull AnvilRecipe recipe) {
        super(player, recipeKey, recipeKey.toString());
        this.anvilRecipe = recipe;
        this.costLevel = recipe.costLevel();
        this.display = new MenuDisplay(
            AnvilEditorConfig.TITLE.value(),
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
    protected void fillRecipeData(Inventory inventory) {
        // base - StackableItemIdChoice, 用getItemStack()获取显示物品
        inventory.setItem(BASE_SLOT, anvilRecipe.base().getItemStack().clone());

        // addition - StackableItemIdChoice
        inventory.setItem(ADDITION_SLOT, anvilRecipe.addition().getItemStack().clone());

        // result - NamespacedItemIdStack, 用ItemManager.matchItem获取ItemStack
        ItemStack resultItem = ItemManager.INSTANCE.matchItem(anvilRecipe.result());
        inventory.setItem(RESULT_SLOT, resultItem.clone());
    }

    private Icon getFrameIcon() {
        return CreatorIconParser.INSTANCE.parse(AnvilEditorConfig.FRAME_ICON.value()).get();
    }

    private Icon getCostLevelIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(
            AnvilEditorConfig.COST_LEVEL_ICON.value()
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
                player.closeInventory();
                new Conversation(Craftorithm.instance(), player,
                    new NumberPrompt() {
                        @Override
                        public Prompt acceptValidatedInput(Map<Object, Object> conversationData, Number input) {
                            costLevel = Math.max(0, input.intValue());
                            return null;
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
                        updateMenu();
                        CrypticLibBukkit.scheduler().sync(() -> openMenu());
                    }
                ).start();
                return this;
            }
        };
    }

    private Icon getConfirmIcon() {
        IconDisplay iconDisplay = CreatorIconParser.INSTANCE.parseIconDisplay(AnvilEditorConfig.CONFIRM_ICON.value());
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
                NamespacedItemIdStack resultId = ItemManager.INSTANCE.matchItemIdOrCreate(result, false);
                if (resultId == null) {
                    LangUtils.sendLang(event.getWhoClicked(), Languages.MENU_RECIPE_CREATOR_NULL_RESULT);
                    return this;
                }

                BukkitConfigWrapper configWrapper = getRecipeConfigWrapper();
                if (configWrapper != null) {
                    configWrapper.set("type", SimpleRecipeTypes.ANVIL.typeKey());
                    configWrapper.set("result", resultId.toString());
                    if (baseId != null) configWrapper.set("base", baseId);
                    if (additionId != null) configWrapper.set("addition", additionId);
                    configWrapper.set("cost_level", costLevel);
                    saveRecipeConfig(configWrapper);
                }

                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }
}
