package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.chat.TextProcessor;
import crypticlib.config.ConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.menu.MultipageMenu;
import crypticlib.util.ItemUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class RecipeGroupEditor extends MultipageMenu {

    protected RecipeGroup recipeGroup;
    protected String title;
    protected int sortId;
    protected final Character ELEMENT_KEY = '%';

    protected RecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player);
        setElementKey(ELEMENT_KEY);
        Validate.notNull(recipeGroup);
        this.recipeGroup = recipeGroup;
        this.title = Languages.MENU_RECIPE_EDITOR_TITLE.value(player).replace("<recipe_name>", recipeGroup.groupName());
    }

    public RecipeGroup recipeGroup() {
        return recipeGroup;
    }

    public RecipeGroupEditor setRecipeGroup(RecipeGroup recipeGroup) {
        this.recipeGroup = recipeGroup;
        return this;
    }

    public String title() {
        return title;
    }

    public RecipeGroupEditor setTitle(String title) {
        this.title = title;
        return this;
    }

    public int sortId() {
        return sortId;
    }

    public RecipeGroupEditor setSortId(int sortId) {
        this.sortId = sortId;
        return this;
    }

    protected Icon getSortIdEditIcon(int slot) {
        return new Icon(
            Material.OAK_SIGN,
            Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_NAME.value(player)
                .replace("<id>", RecipeManager.INSTANCE.getRecipeGroupSortId(recipeGroup.groupName()) + ""),
            Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_LORE.value(player),
            event -> {
                new Conversation(
                    Craftorithm.instance(),
                    player,
                    new SortIdEditPrompt(event.getInventory().getItem(slot))
                ).start();
                player.closeInventory();
            }
        );
    }

    protected void updateSortIdEditIcon(ItemStack sortIdEditIconDisplay) {
        ItemUtil.setDisplayName(
            sortIdEditIconDisplay,
            TextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_NAME
                .value(player)
                .replace("<id>", RecipeManager.INSTANCE.getRecipeGroupSortId(recipeGroup.groupName()) + ""))
        );
    }

    protected class SortIdEditPrompt implements NumberPrompt {

        protected ItemStack sortIdEditIconDisplay;

        public SortIdEditPrompt(ItemStack sortIdEditIconDisplay) {
            this.sortIdEditIconDisplay = sortIdEditIconDisplay;
        }

        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            int sortId = number.intValue();
            recipeGroup.setSortId(sortId);
            ConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
            configWrapper.set("sort_id", sortId);
            configWrapper.saveConfig();
            updateSortIdEditIcon(sortIdEditIconDisplay);
            openMenu();
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return TextProcessor.toComponent(TextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_INPUT_HINT.value(player)));
        }

        public ItemStack sortIdEditIconDisplay() {
            return sortIdEditIconDisplay;
        }

        public SortIdEditPrompt setSortIdEditIconDisplay(ItemStack sortIdEditIconDisplay) {
            this.sortIdEditIconDisplay = sortIdEditIconDisplay;
            return this;
        }

    }

    public Icon getFrameIcon() {
        return new Icon(Material.BLACK_STAINED_GLASS_PANE, Languages.MENU_RECIPE_EDITOR_ICON_FRAME.value(player));
    }

    public Icon getNextIcon() {
        return new Icon(Material.PAPER, Languages.MENU_RECIPE_EDITOR_ICON_NEXT.value(player), event -> nextPage());
    }

    public Icon getPreviousIcon() {
        return new Icon(Material.PAPER, Languages.MENU_RECIPE_EDITOR_ICON_PREVIOUS.value(player), event -> previousPage());
    }

}
