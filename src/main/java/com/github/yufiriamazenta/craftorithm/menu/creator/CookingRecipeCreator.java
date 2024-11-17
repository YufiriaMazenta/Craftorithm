package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class CookingRecipeCreator extends UnlockableRecipeCreator {

    private final Map<Material, Boolean> cookingToggleMap;
    private int cookingTime;
    private float exp;
    private boolean inConversation = false;

    public CookingRecipeCreator(@NotNull Player player, @NotNull String recipeName) {
        super(player, RecipeType.COOKING, recipeName);
        this.cookingTime = 200;
        this.exp = 0;
        this.cookingToggleMap = new HashMap<>();
        this.cookingToggleMap.put(Material.FURNACE, true);
        this.cookingToggleMap.put(Material.BLAST_FURNACE, false);
        this.cookingToggleMap.put(Material.SMOKER, false);
        this.cookingToggleMap.put(Material.CAMPFIRE, false);
        setDisplay(
            new MenuDisplay(
                title(),
                new MenuLayout(Arrays.asList(
                    "###GFH###",
                    "#***#%%%#",
                    "#* *A% %#",
                    "#***#%%%#",
                    "##BC#DE##"
                ), () -> {
                    Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                    layoutMap.put('#', this::getFrameIcon);
                    layoutMap.put('%', this::getResultFrameIcon);
                    layoutMap.put('*', () -> new Icon(new IconDisplay(
                        Material.CYAN_STAINED_GLASS_PANE,
                        Languages.MENU_RECIPE_CREATOR_ICON_COOKING_FRAME.value(player)
                    )));
                    layoutMap.put('B', () -> getCookingToggleIcon(Material.FURNACE));
                    layoutMap.put('C', () -> getCookingToggleIcon(Material.BLAST_FURNACE));
                    layoutMap.put('D', () -> getCookingToggleIcon(Material.SMOKER));
                    layoutMap.put('E', () -> getCookingToggleIcon(Material.CAMPFIRE));
                    layoutMap.put('F', this::getUnlockIcon);
                    layoutMap.put('G', this::getCookingTimeIcon);
                    layoutMap.put('H', this::getExpIcon);
                    layoutMap.put('A', () -> new Icon(
                        new IconDisplay(Material.FURNACE, Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player))
                        ).setClickAction(
                        event -> {
                            StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                            ItemStack source = Objects.requireNonNull(creator).storedItems().get(20);
                            ItemStack result = creator.storedItems().get(24);
                            if (ItemHelper.isAir(source)) {
                                LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                return;
                            }
                            if (ItemHelper.isAir(result)) {
                                LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                return;
                            }
                            String sourceName = ItemUtils.matchItemNameOrCreate(source, true);
                            String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                            BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                            recipeConfig.set("type", "cooking");
                            recipeConfig.set("result", resultName);
                            recipeConfig.set("multiple", true);
                            List<Map<String, Object>> sourceList = new ArrayList<>();
                            cookingToggleMap.forEach(
                                (type, enable) -> {
                                    if (!enable)
                                        return;
                                    Map<String, Object> sourceMap = new HashMap<>();
                                    sourceMap.put("block", type.name().toLowerCase());
                                    sourceMap.put("item", sourceName);
                                    sourceMap.put("time", cookingTime);
                                    sourceMap.put("exp", exp);
                                    sourceList.add(sourceMap);
                                }
                            );
                            recipeConfig.set("unlock", unlock());
                            recipeConfig.set("source", sourceList);
                            recipeConfig.saveConfig();
                            recipeConfig.reloadConfig();
                            regRecipeGroup(recipeConfig);
                            event.getWhoClicked().closeInventory();
                            sendSuccessMsg(event.getWhoClicked(), recipeName);
                        }
                        )
                    );
                    return layoutMap;
                })
            )
        );
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!inConversation) {
            super.onClose(event);
        } else {
            this.refreshStoredItems(event.getInventory());
        }
    }

    protected Icon getCookingToggleIcon(Material material) {
        boolean enable = cookingToggleMap.getOrDefault(material, false);
        String displayName = switch (material) {
            default -> Languages.MENU_RECIPE_CREATOR_ICON_FURNACE_TOGGLE.value(player);
            case BLAST_FURNACE -> Languages.MENU_RECIPE_CREATOR_ICON_BLAST_FURNACE_TOGGLE.value(player);
            case SMOKER -> Languages.MENU_RECIPE_CREATOR_ICON_SMOKER_TOGGLE.value(player);
            case CAMPFIRE -> Languages.MENU_RECIPE_CREATOR_ICON_CAMPFIRE_TOGGLE.value(player);
        };
        displayName = displayName.replace("<enable>", String.valueOf(enable));
        Icon icon = new Icon(
            new IconDisplay(
                material,
                displayName
            )
        ).setClickAction(
            event -> toggleCookingIcon(event.getSlot(), event)
        );
        if (enable) {
            ItemUtils.toggleItemGlowing(icon.display());
        }
        return icon;
    }

    protected void toggleCookingIcon(int slot, InventoryClickEvent event) {
        super.toggleIconGlowing(slot, event);
        ItemStack display = event.getCurrentItem();
        String displayName = switch (display.getType()) {
            default -> Languages.MENU_RECIPE_CREATOR_ICON_FURNACE_TOGGLE.value(player);
            case BLAST_FURNACE -> Languages.MENU_RECIPE_CREATOR_ICON_BLAST_FURNACE_TOGGLE.value(player);
            case SMOKER -> Languages.MENU_RECIPE_CREATOR_ICON_SMOKER_TOGGLE.value(player);
            case CAMPFIRE -> Languages.MENU_RECIPE_CREATOR_ICON_CAMPFIRE_TOGGLE.value(player);
        };
        boolean enable = !cookingToggleMap.getOrDefault(display.getType(), false);
        cookingToggleMap.put(display.getType(), enable);

        ItemHelper.setDisplayName(display, displayName.replace("<enable>", String.valueOf(enable)));
    }

    protected Icon getCookingTimeIcon() {
        return new Icon(
            new IconDisplay(
                Material.CLOCK,
                Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_NAME.value(player)
                    .replace("<time>", String.valueOf(cookingTime)),
                Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_LORE.value(player)
            )
        ).setClickAction(
            event -> {
                Conversation timeInputConversation = new Conversation(
                    Craftorithm.instance(),
                    player,
                    new TimeInputPrompt(),
                    data -> player.openInventory(inventoryCache)
                );
                inConversation = true;
                timeInputConversation.start();
                player.closeInventory();
            }
        );
    }

    protected Icon getExpIcon() {
        return new Icon(
            new IconDisplay(
                Material.EXPERIENCE_BOTTLE,
                Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_NAME.value(player)
                    .replace("<exp>", String.valueOf(exp)),
                Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_LORE.value(player)
            )
        ).setClickAction(
            event -> {
                Conversation conversation = new Conversation(
                    Craftorithm.instance(),
                    player,
                    new ExpInputPrompt(),
                    data -> player.openInventory(inventoryCache)
                );
                inConversation = true;
                conversation.start();
                player.closeInventory();
            }
        );
    }

    protected void updateCookingTimeIcon() {
        ItemStack cookingTimeIcon = this.inventoryCache.getItem(3);
        if (cookingTimeIcon == null)
            return;
        ItemHelper.setDisplayName(
            cookingTimeIcon,
            Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_NAME
                .value(player)
                .replace("<time>", String.valueOf(cookingTime)
        ));
    }

    protected void updateExpIcon() {
        ItemStack expIcon = this.inventoryCache.getItem(5);
        if (expIcon == null)
            return;
        ItemHelper.setDisplayName(
            expIcon,
            Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_NAME.value(player)
                .replace("<exp>", String.valueOf(exp)
        ));
    }

    class TimeInputPrompt implements NumberPrompt {

        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            cookingTime = number.intValue();
            updateCookingTimeIcon();
            openMenu();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return BukkitTextProcessor.toComponent(BukkitTextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_INPUT_HINT.value(player)));
        }
    }

    class ExpInputPrompt implements NumberPrompt {
        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            exp = number.floatValue();
            updateExpIcon();
            openMenu();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return BukkitTextProcessor.toComponent(BukkitTextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_INPUT_HINT.value(player)));
        }
    }

}
