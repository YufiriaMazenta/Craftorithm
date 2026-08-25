package pers.yufiria.craftorithm.ui.display.vanillaStonecutting;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaStonecuttingDisplay;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Optional;

public class VanillaStonecuttingDisplayMenu extends RecipeDisplayMenu<StonecuttingRecipe> {

    public VanillaStonecuttingDisplayMenu(@NotNull Player player, StonecuttingRecipe recipe) {
        super(player, recipe);
        setDisplay(
            loadMenuDisplay(
                VanillaStonecuttingDisplay.TITLE.value(),
                VanillaStonecuttingDisplay.LAYOUT.value(),
                VanillaStonecuttingDisplay.ICONS.value()
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
                    case VanillaStonecuttingDisplayIconParser.ICON_TYPE_INGREDIENT -> {
                        itemDisplayIcon.setDisplayItem(recipe.getInputChoice().getItemStack());
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
        return VanillaStonecuttingDisplayIconParser.INSTANCE;
    }

}
