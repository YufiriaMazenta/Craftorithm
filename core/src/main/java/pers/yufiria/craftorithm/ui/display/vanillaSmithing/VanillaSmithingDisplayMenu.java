package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.MinecraftVersion;
import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmithingDisplay;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Optional;

public class VanillaSmithingDisplayMenu extends RecipeDisplayMenu<SmithingRecipe> {

    public VanillaSmithingDisplayMenu(@NotNull Player player, SmithingRecipe recipe) {
        super(player, recipe);
        setDisplay(
            loadMenuDisplay(
                VanillaSmithingDisplay.TITLE.value(),
                VanillaSmithingDisplay.LAYOUT.value(),
                VanillaSmithingDisplay.ICONS.value()
            )
        );
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ItemDisplayIcon itemDisplayIcon) {
            Optional<Object> iconTypeOpt = itemDisplayIcon.getData("icon_type");
            iconTypeOpt.ifPresent(obj -> {
                String iconType = obj.toString();
                switch (iconType.toLowerCase()) {
                    case VanillaSmithingDisplayIconParser.ICON_TYPE_TEMPLATE -> {
                        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20)) {
                            if (recipe instanceof SmithingTransformRecipe transformRecipe) {
                                itemDisplayIcon.setDisplayItem(transformRecipe.getTemplate().getItemStack());
                            } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
                                itemDisplayIcon.setDisplayItem(trimRecipe.getTemplate().getItemStack());
                            } else {
                                itemDisplayIcon.setDisplayItem(null);
                            }
                        } else {
                            itemDisplayIcon.setDisplayItem(null);
                        }
                    }
                    case VanillaSmithingDisplayIconParser.ICON_TYPE_BASE -> {
                        itemDisplayIcon.setDisplayItem(recipe.getBase().getItemStack());
                    }
                    case VanillaSmithingDisplayIconParser.ICON_TYPE_ADDITION -> {
                        itemDisplayIcon.setDisplayItem(recipe.getAddition().getItemStack());
                    }
                    case RecipeDisplayManager.ICON_TYPE_RESULT -> {
                        itemDisplayIcon.setDisplayItem(recipe.getResult());
                    }
                }
            });
        }
    }

    @Override
    public IconParser iconParser() {
        return VanillaSmithingDisplayIconParser.INSTANCE;
    }
}
