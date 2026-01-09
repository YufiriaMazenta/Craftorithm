package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.menu.display.RecipeGroupListMenu;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class UnlockableRecipeGroupEditor extends RecipeGroupEditor {

    protected boolean unlock;

    protected UnlockableRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup, RecipeGroupListMenu parent) {
        super(player, recipeGroup, parent);
        this.unlock = recipeGroup.unlock();
    }

    protected Icon getUnlockIcon() {
        Icon icon = new Icon(
            new IconDisplay(
                Material.KNOWLEDGE_BOOK,
                Languages.MENU_RECIPE_EDITOR_ICON_UNLOCK.value(player)
                    .replace("<unlock>", String.valueOf(unlock))
            )
        ).setClickAction(
            event -> {
                unlock = !unlock;
                recipeGroup.setUnlock(unlock);
                recipeGroup.recipeGroupConfig().set("unlock", unlock);
                recipeGroup.recipeGroupConfig().saveConfig();
                if (RecipeManager.UNLOCKABLE_RECIPE_TYPE.contains(recipeGroup.recipeType())) {
                    for (NamespacedKey recipeKey : recipeGroup.groupRecipeKeys()) {
                        RecipeManager.INSTANCE.recipeUnlockMap().put(recipeKey, unlock);
                    }
                }
                updateUnlockIcon(event.getInventory().getItem(event.getSlot()));
            }
        );
        if (recipeGroup.unlock()) {
            ItemUtils.toggleItemGlowing(icon.display());
        }
        return icon;
    }

    protected void updateUnlockIcon(ItemStack unlockIconDisplay) {
        if (ItemHelper.isAir(unlockIconDisplay)) return;
        ItemUtils.toggleItemGlowing(unlockIconDisplay);
        ItemHelper.setDisplayName(
            unlockIconDisplay,
            Languages.MENU_RECIPE_EDITOR_ICON_UNLOCK.value(player)
                .replace("<unlock>", String.valueOf(unlock))
        );
    }

}
