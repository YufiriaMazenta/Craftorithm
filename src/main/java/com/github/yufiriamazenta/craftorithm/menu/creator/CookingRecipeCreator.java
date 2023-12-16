package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.chat.TextProcessor;
import crypticlib.chat.entry.StringLangConfigEntry;
import crypticlib.config.ConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
                    Map<Character, Icon> layoutMap = new HashMap<>();
                    layoutMap.put('#', getFrameIcon());
                    layoutMap.put('%', getResultFrameIcon());
                    layoutMap.put('*', new Icon(
                        Material.CYAN_STAINED_GLASS_PANE,
                        Languages.MENU_RECIPE_CREATOR_ICON_COOKING_FRAME.value(player)
                    ));
                    layoutMap.put('B', getCookingToggleIcon(Material.FURNACE));
                    layoutMap.put('C', getCookingToggleIcon(Material.BLAST_FURNACE));
                    layoutMap.put('D', getCookingToggleIcon(Material.SMOKER));
                    layoutMap.put('E', getCookingToggleIcon(Material.CAMPFIRE));
                    layoutMap.put('F', getUnlockIcon());
                    layoutMap.put('G', getCookingTimeIcon());
                    layoutMap.put('H', getExpIcon());
                    layoutMap.put('A', new Icon(
                        Material.FURNACE,
                        Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player),
                        event -> {
                            StoredMenu creator = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
                            ItemStack source = Objects.requireNonNull(creator).storedItems().get(20);
                            ItemStack result = creator.storedItems().get(24);
                            if (ItemUtil.isAir(source)) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                return;
                            }
                            if (ItemUtil.isAir(result)) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                return;
                            }
                            String sourceName = ItemUtils.matchItemNameOrCreate(source, true);
                            String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                            ConfigWrapper recipeConfig = createRecipeConfig(recipeName);
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
                            for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                                recipeRegistry.register();
                            }
                            RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                            event.getWhoClicked().closeInventory();
                            sendSuccessMsg(event.getWhoClicked(), recipeName);
                        })
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
        String displayName;
        switch (material) {
            case FURNACE:
            default:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_FURNACE_TOGGLE.value(player());
                break;
            case BLAST_FURNACE:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_BLAST_FURNACE_TOGGLE.value(player());
                break;
            case SMOKER:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_SMOKER_TOGGLE.value(player());
                break;
            case CAMPFIRE:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_CAMPFIRE_TOGGLE.value(player());
                break;
        }
        displayName = displayName.replace("<enable>", String.valueOf(enable));
        Icon icon = new Icon(
            material,
            displayName,
            event -> toggleCookingIcon(event.getSlot(), event)
        );
        if (enable) {
            toggleItemGlowing(icon.display());
        }
        return icon;
    }

    protected void toggleCookingIcon(int slot, InventoryClickEvent event) {
        super.toggleIconGlowing(slot, event);
        ItemStack display = event.getCurrentItem();
        String displayName;
        switch (display.getType()) {
            case FURNACE:
            default:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_FURNACE_TOGGLE.value(player());
                break;
            case BLAST_FURNACE:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_BLAST_FURNACE_TOGGLE.value(player());
                break;
            case SMOKER:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_SMOKER_TOGGLE.value(player());
                break;
            case CAMPFIRE:
                displayName = Languages.MENU_RECIPE_CREATOR_ICON_CAMPFIRE_TOGGLE.value(player());
                break;
        }
        boolean enable = !cookingToggleMap.getOrDefault(display.getType(), false);
        cookingToggleMap.put(display.getType(), enable);

        ItemMeta itemMeta = display.getItemMeta();
        itemMeta.setDisplayName(
            TextProcessor.color(
                displayName.replace("<enable>", String.valueOf(enable))
            )
        );
        display.setItemMeta(itemMeta);
    }

    protected Icon getCookingTimeIcon() {
        Icon icon = new Icon(
            Material.CLOCK,
            TextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_NAME.value(player()))
                .replace("<time>", String.valueOf(cookingTime)),
            event -> {
                Player player = (Player) event.getWhoClicked();
                Conversation timeInputConversation = new Conversation(
                    Craftorithm.instance(),
                    player,
                    new CookingTimePrompt(),
                    data -> player().openInventory(openedInventory())
                );
                inConversation = true;
                timeInputConversation.start();
                player.closeInventory();
            }
        );
        ItemUtils.setLore(icon.display(), Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_LORE.value(player()));
        return icon;
    }

    protected Icon getExpIcon() {
        Icon icon = new Icon(
            Material.EXPERIENCE_BOTTLE,
            TextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_NAME.value(player()))
                .replace("<exp>", String.valueOf(exp)),
            event -> {
                Player player = (Player) event.getWhoClicked();
                Conversation conversation = new Conversation(
                    Craftorithm.instance(),
                    player,
                    new ExpPrompt(),
                    data -> player().openInventory(openedInventory())
                );
                inConversation = true;
                conversation.start();
                player.closeInventory();
            }
        );
        ItemUtils.setLore(icon.display(), Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_LORE.value(player()));
        return icon;
    }

    protected void updateCookingTimeIcon() {
        ItemStack cookingTimeIcon = this.openedInventory().getItem(3);
        if (cookingTimeIcon == null)
            return;
        ItemMeta itemMeta = cookingTimeIcon.getItemMeta();
        itemMeta.setDisplayName(TextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_NAME.value(player()))
            .replace("<time>", String.valueOf(cookingTime)));
        cookingTimeIcon.setItemMeta(itemMeta);
    }

    protected void updateExpIcon() {
        ItemStack expIcon = this.openedInventory().getItem(5);
        if (expIcon == null)
            return;
        ItemMeta itemMeta = expIcon.getItemMeta();
        itemMeta.setDisplayName(TextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_NAME.value(player()))
            .replace("<exp>", String.valueOf(exp)));
        expIcon.setItemMeta(itemMeta);
    }

    class CookingTimePrompt implements NumberPrompt {

        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            cookingTime = number.intValue();
            player().openInventory(openedInventory());
            updateCookingTimeIcon();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull StringLangConfigEntry promptText(@NotNull Map<Object, Object> data) {
            return Languages.MENU_RECIPE_CREATOR_ICON_COOKING_TIME_INPUT_HINT;
        }
    }

    class ExpPrompt implements NumberPrompt {
        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            exp = number.intValue();
            player().openInventory(openedInventory());
            updateExpIcon();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull StringLangConfigEntry promptText(@NotNull Map<Object, Object> data) {
            return Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_INPUT_HINT;
        }
    }

}
