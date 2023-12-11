package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeFactory;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import crypticlib.config.yaml.YamlConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.StoredMenu;
import crypticlib.util.ItemUtil;
import crypticlib.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                    "####B####"
                ), () -> {
                    Map<Character, Icon> layoutMap = new HashMap<>();
                    layoutMap.put('#', getFrameIcon());
                    layoutMap.put('%', getResultFrameIcon());
                    layoutMap.put('*', new Icon(Material.CYAN_STAINED_GLASS_PANE, Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_FRAME.value()));
                    layoutMap.put('B', getCopyNbtIcon());
                    layoutMap.put('C', getCostLevelIcon());
                    layoutMap.put('A', new Icon(Material.ANVIL, Languages.MENU_RECIPE_CREATOR_ICON_CONFIRM.value(),
                        event -> {
                            StoredMenu creator = (StoredMenu) event.getClickedInventory().getHolder();
                            ItemStack result = Objects.requireNonNull(creator).storedItems().get(24);
                            ItemStack base = creator.storedItems().get(19);
                            ItemStack addition = creator.storedItems().get(21);
                            if (ItemUtil.isAir(result)) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_RESULT.value());
                                return;
                            }
                            if (ItemUtil.isAir(addition) || ItemUtil.isAir(base)) {
                                LangUtil.sendLang(event.getWhoClicked(), Languages.COMMAND_CREATE_NULL_SOURCE.value());
                                return;
                            }
                            String resultName = ItemUtils.matchItemNameOrCreate(result, false);
                            String inputName = ItemUtils.matchItemNameOrCreate(base, false);
                            String ingredientName = ItemUtils.matchItemNameOrCreate(addition, false);
                            YamlConfigWrapper recipeConfig = createRecipeConfig(recipeName);
                            recipeConfig.set("source.copy_nbt", event.getInventory().getItem(40).getItemMeta().hasEnchants());
                            recipeConfig.set("type", "anvil");
                            recipeConfig.set("source.base", inputName);
                            recipeConfig.set("source.addition", ingredientName);
                            recipeConfig.set("source.cost_level", costLevel);
                            recipeConfig.set("result", resultName);
                            recipeConfig.saveConfig();
                            recipeConfig.reloadConfig();
                            for (RecipeRegistry recipeRegistry : RecipeFactory.newRecipeRegistry(recipeConfig.config(), recipeName)) {
                                recipeRegistry.register();
                            }
                            RecipeManager.INSTANCE.recipeConfigWrapperMap().put(recipeName, recipeConfig);
                            event.getWhoClicked().closeInventory();
                            sendSuccessMsg(player, recipeName);
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

    protected Icon getCopyNbtIcon() {
        Icon icon = new Icon(
            Material.NAME_TAG,
            Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COPY_NBT_TOGGLE
                .value()
                .replace("<enable>", String.valueOf(copyNbt)),
            event -> toggleCopyNbt(event.getSlot(), event)
        );
        if (copyNbt)
            toggleItemGlowing(icon.display());
        return icon;
    }

    protected void toggleCopyNbt(int slot, InventoryClickEvent event) {
        super.toggleIconGlowing(slot, event);
        copyNbt = !copyNbt;
        ItemStack display = event.getCurrentItem();
        ItemMeta itemMeta = display.getItemMeta();
        itemMeta.setDisplayName(
            TextUtil.color(
                Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COPY_NBT_TOGGLE
                    .value()
                    .replace("<enable>", String.valueOf(copyNbt))
            )
        );
        display.setItemMeta(itemMeta);
    }

    protected Icon getCostLevelIcon() {
        Icon icon = new Icon(
            Material.EXPERIENCE_BOTTLE,
            TextUtil.color(Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_NAME.value())
                .replace("<level>", String.valueOf(costLevel)),
            event -> {
                Player player = (Player) event.getWhoClicked();
                Conversation conversation = new Conversation(
                    Craftorithm.instance(),
                    player,
                    new CostLevelPrompt(),
                    () -> player().openInventory(openedInventory())
                );
                inConversation = true;
                conversation.start();
                player.closeInventory();
            }
        );
        icon.display().setLore(Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_LORE.value());
        icon.display().getLore().replaceAll(TextUtil::color);
        return icon;
    }

    public void updateCostLevelIcon() {
        ItemStack costLevelIcon = this.openedInventory().getItem(4);
        if (costLevelIcon == null)
            return;
        ItemMeta itemMeta = costLevelIcon.getItemMeta();
        itemMeta.setDisplayName(TextUtil.color(Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_NAME.value())
            .replace("<level>", String.valueOf(costLevel)));
        costLevelIcon.setItemMeta(itemMeta);
    }

    class CostLevelPrompt implements NumberPrompt {
        @Override
        @Nullable
        public Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            costLevel = number.intValue();
            player().openInventory(openedInventory());
            updateCostLevelIcon();
            inConversation = false;
            return null;
        }

        @Override
        public @NotNull String promptText(@NotNull Map<Object, Object> data) {
            return TextUtil.color(Languages.MENU_RECIPE_CREATOR_ICON_ANVIL_COST_LEVEL_INPUT_HINT.value());
        }
    }

}
