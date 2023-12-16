package com.github.yufiriamazenta.craftorithm.menu.creator;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import crypticlib.chat.TextProcessor;
import crypticlib.ui.display.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public abstract class UnlockableRecipeCreator extends RecipeCreator {

    private boolean unlock;

    public UnlockableRecipeCreator(@NotNull Player player, @NotNull RecipeType recipeType, @NotNull String recipeName) {
        super(player, recipeType, recipeName);
        unlock = PluginConfigs.DEFAULT_RECIPE_UNLOCK.value();
    }

    protected Icon getUnlockIcon() {
        Icon icon = new Icon(
            Material.KNOWLEDGE_BOOK,
            Languages.MENU_RECIPE_CREATOR_ICON_UNLOCK.value().replace("<unlock>", String.valueOf(unlock)),
            event -> toggleUnlockIcon(event.getSlot(), event)
        );
        if (unlock) {
            toggleItemGlowing(icon.display());
        }
        return icon;
    }

    public boolean unlock() {
        return unlock;
    }

    public UnlockableRecipeCreator setUnlock(boolean unlock) {
        this.unlock = unlock;
        return this;
    }

    protected void toggleUnlockIcon(int slot, InventoryClickEvent event) {
        super.toggleIconGlowing(slot, event);
        unlock = !unlock;
        ItemStack display = event.getCurrentItem();
        ItemMeta itemMeta = display.getItemMeta();
        itemMeta.setDisplayName(
            TextProcessor.color(
                Languages.MENU_RECIPE_CREATOR_ICON_UNLOCK
                    .value()
                    .replace("<unlock>", String.valueOf(unlock))
            )
        );
        display.setItemMeta(itemMeta);
    }

}
