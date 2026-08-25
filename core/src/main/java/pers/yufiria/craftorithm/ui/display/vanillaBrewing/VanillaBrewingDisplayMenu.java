package pers.yufiria.craftorithm.ui.display.vanillaBrewing;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaBrewingDisplay;
import pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Optional;

public class VanillaBrewingDisplayMenu extends RecipeDisplayMenu<BrewingRecipe> {

    public VanillaBrewingDisplayMenu(@NotNull Player player, BrewingRecipe recipe) {
        super(player, recipe);
        setDisplay(loadMenuDisplay(
            VanillaBrewingDisplay.TITLE.value(),
            VanillaBrewingDisplay.LAYOUT.value(),
            VanillaBrewingDisplay.ICONS.value()
        ));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ItemDisplayIcon itemDisplayIcon) {
            Optional<Object> iconTypeOpt = itemDisplayIcon.getData("icon_type");
            iconTypeOpt.ifPresent(obj -> {
                String iconType = obj.toString();
                switch (iconType.toLowerCase()) {
                    case VanillaBrewingDisplayIconParser.ICON_TYPE_INGREDIENT -> {
                        itemDisplayIcon.setDisplayItem(recipe.ingredient().getItemStack());
                    }
                    case VanillaBrewingDisplayIconParser.ICON_TYPE_INPUT -> {
                        itemDisplayIcon.setDisplayItem(recipe.input().getItemStack());
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
        return VanillaBrewingDisplayIconParser.INSTANCE;
    }

}
