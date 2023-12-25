package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.ui.display.Icon;
import crypticlib.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class UnlockableRecipeGroupEditor extends RecipeGroupEditor {

    protected boolean unlock;

    protected UnlockableRecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player, recipeGroup);
        this.unlock = recipeGroup.unlock();
    }

    protected Icon getUnlockIcon() {
        Icon icon = new Icon(
            Material.KNOWLEDGE_BOOK,
            Languages.MENU_RECIPE_EDITOR_ICON_UNLOCK.value(player)
                .replace("<unlock>", String.valueOf(unlock)),
            event -> {
                unlock = !unlock;
                recipeGroup.setUnlock(unlock);
                recipeGroup.recipeGroupConfig().set("unlock", unlock);
                recipeGroup.recipeGroupConfig().saveConfig();
                updateUnlockIcon(event.getInventory().getItem(event.getSlot()));
            }
        );
        if (recipeGroup.unlock()) {
            ItemUtils.toggleItemGlowing(icon.display());
        }
        return icon;
    }

    protected void updateUnlockIcon(ItemStack unlockIconDisplay) {
        if (ItemUtil.isAir(unlockIconDisplay)) return;
        ItemUtils.toggleItemGlowing(unlockIconDisplay);
        ItemUtil.setDisplayName(
            unlockIconDisplay,
            Languages.MENU_RECIPE_EDITOR_ICON_UNLOCK.value(player)
                .replace("<unlock>", String.valueOf(unlock))
        );
    }

}
