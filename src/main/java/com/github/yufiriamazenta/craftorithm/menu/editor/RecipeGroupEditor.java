package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.sun.tools.javac.jvm.Items;
import crypticlib.chat.TextProcessor;
import crypticlib.config.ConfigWrapper;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RecipeGroupEditor extends Menu {

    private final RecipeGroup recipeGroup;

    public RecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player);
        this.recipeGroup = recipeGroup;
        Validate.notNull(recipeGroup);
        setDisplay(new MenuDisplay(
            Languages.MENU_RECIPE_EDITOR_TITLE.value(player).replace("<recipe_name>", recipeGroup.groupName()),
            new MenuLayout(
                Collections.singletonList(
                    "ABCDEFGHI"
                ),
                () -> {
                    Map<Character, Icon> iconMap = new HashMap<>();
                    iconMap.put('A', sortIdEditIcon());
                    switch (recipeGroup.recipeType()) {

                    }
                    return iconMap;
                }
            )
        ));
    }

    private Icon sortIdEditIcon() {
        return new Icon(
            Material.OAK_SIGN,
            Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_NAME.value(player)
                .replace("<id>", RecipeManager.INSTANCE.getRecipeGroupSortId(recipeGroup.groupName()) + ""),
            Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_LORE.value(player),
            event -> {
                new Conversation(
                    Craftorithm.instance(),
                    player,
                    new SortIdEditPrompt()
                ).start();
                player.closeInventory();
            }
        );
    }

    private void updateSortIdEditIcon() {
        ItemStack sortIdEditIcon = openedInventory.getItem(0);
        ItemMeta itemMeta = sortIdEditIcon.getItemMeta();
        itemMeta.setDisplayName(TextProcessor.color(Languages.MENU_RECIPE_EDITOR_ICON_SORT_ID_NAME.value(player)
            .replace("<id>", RecipeManager.INSTANCE.getRecipeGroupSortId(recipeGroup.groupName()) + "")));
        sortIdEditIcon.setItemMeta(itemMeta);
    }

    class SortIdEditPrompt implements NumberPrompt {
        @Override
        public @Nullable Prompt acceptValidatedInput(@NotNull Map<Object, Object> data, @NotNull Number number) {
            int sortId = number.intValue();
            RecipeManager.INSTANCE.setRecipeGroupSortId(recipeGroup.groupName(), sortId);
            ConfigWrapper configWrapper = recipeGroup.recipeGroupConfig();
            configWrapper.set("sort_id", sortId);
            configWrapper.saveConfig();
            updateSortIdEditIcon();
            openMenu();
            return null;
        }

        @Override
        public @NotNull BaseComponent promptText(@NotNull Map<Object, Object> data) {
            return TextProcessor.toComponent(TextProcessor.color(Languages.MENU_RECIPE_CREATOR_ICON_COOKING_EXP_INPUT_HINT.value(player)));
        }
    }

}
