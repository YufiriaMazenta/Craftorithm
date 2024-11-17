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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class AnvilRecipeCreator extends RecipeCreator {

    private boolean copyNbt;
    private int costLevel;
    private boolean inConversation;

    public AnvilRecipeCreator(@NotNull Player player, @NotNull String recipeName) {
        super(player, RecipeType.ANVIL, recipeName);
        this.copyNbt = true;
        this.inConversation = false;
        this.costLevel = 0;
        setDisplay(
            new MenuDisplay(
                title(),
                new MenuLayout(Arrays.asList(
                    "####C####",
                    "#***#%%%#",
                    "# * A% %#",
                    "#***#%%%#",
                    "#########"
                ), () -> {
                    Map<Character, Supplier<Icon>> layoutMap = new HashMap<>();
                    layoutMap.put('#', this::getFrameIcon);
                    layoutMap.put('%', this::getResultFrameIcon);
                    layoutMap.put('*', () -> new Icon(new IconDisplay(Material.CYAN_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_FRAME.value(player))));
                    layoutMap.put('B', this::getCopyNbtIcon);
                    layoutMap.put('C', this::getCostLevelIcon);
                    layoutMap.put('A', () -> new Icon(new IconDisplay(Material.ANVIL, Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(player)))
                        .setClickAction(
                            event -> {
                                StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                                ItemStack result = Objects.requireNonNull(creator).storedItems().get(24);
                                ItemStack base = creator.storedItems().get(19);
                                ItemStack addition = creator.storedItems().get(21);
                                if (ItemHelper.isAir(result)) {
                                    LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT);
                                    return;
                                }
                                if (ItemHelper.isAir(addition) || ItemHelper.isAir(base)) {
                                    LangUtils.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE);
                                    return;
                                }
                                String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                                String inputName = ItemUtils.matchItemNameOrCreate(base, false);
                                String ingredientName = ItemUtils.matchItemNameOrCreate(addition, false);
                                BukkitConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                                recipeConfig.set("source.copy_nbt", event.getInventory().getItem(40).getItemMeta().hasEnchants());
                                recipeConfig.set("type", "anvil");
                                recipeConfig.set("source.base", inputName);
                                recipeConfig.set("source.addition", ingredientName);
                                recipeConfig.set("source.cost_level", costLevel);
                                recipeConfig.set("result", resultName);
                                recipeConfig.saveConfig();
                                recipeConfig.reloadConfig();
                                regRecipeGroup(recipeConfig);
                                event.getWhoClicked().closeInventory();
                                sendSuccessMsg(player, recipeName);
                            }));
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

    protected Icon getCopyNbtIcon() {
        Icon icon = new Icon(
            new IconDisplay(
                Material.NAME_TAG,
                Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COPY_NBT_TOGGLE
                    .value(player)
                    .replace("<enable>", String.valueOf(copyNbt))
            )
        ).setClickAction(
            event -> toggleCopyNbt(event.getSlot(), event)
        );
        if (copyNbt)
            ItemUtils.toggleItemGlowing(icon.display());
        return icon;
    }

    protected void toggleCopyNbt(int slot, InventoryClickEvent event) {
        super.toggleIconGlowing(slot, event);
        copyNbt = !copyNbt;
        ItemStack display = event.getCurrentItem();
        ItemHelper.setDisplayName(
            display,
            Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COPY_NBT_TOGGLE
                .value(player)
                .replace("<enable>", String.valueOf(copyNbt))
        );
    }

    protected Icon getCostLevelIcon() {
        return new Icon(
            new IconDisplay(
                Material.EXPERIENCE_BOTTLE,
                Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_NAME.value(player)
                    .replace("<level>", String.valueOf(costLevel)),
                Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_LORE.value(player)
            )
        ).setClickAction(
            event -> {
                Conversation conversation = new Conversation(
                    Craftorithm.instance(),
                    player,
                    new CostLevelInputPrompt(),
                    data -> player.openInventory(inventoryCache)
                );
                inConversation = true;
                conversation.start();
                player.closeInventory();
            }
        );
    }

    public void updateCostLevelIcon() {
        ItemStack costLevelIcon = this.inventoryCache.getItem(4);
        if (costLevelIcon == null)
            return;
        ItemHelper.setDisplayName(
            costLevelIcon,
            Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_NAME
                .value(player)
                .replace("<level>", String.valueOf(costLevel))
        );
    }

    class CostLevelInputPrompt implements NumberPrompt {
        @Override
        @Nullable
        public Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            costLevel = number.intValue();
            player.openInventory(inventoryCache);
            updateCostLevelIcon();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return BukkitTextProcessor.toComponent(BukkitTextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_INPUT_HINT.value(player)));
        }
    }

}
