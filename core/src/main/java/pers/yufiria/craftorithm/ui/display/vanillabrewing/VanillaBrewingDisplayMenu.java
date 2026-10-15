package pers.yufiria.craftorithm.ui.display.vanillabrewing;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaBrewingDisplayConfig;
import pers.yufiria.craftorithm.recipe.BrewingRecipe;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;

public class VanillaBrewingDisplayMenu extends RecipeDisplayMenu<BrewingRecipe> {

    public VanillaBrewingDisplayMenu(@NotNull Player player, BrewingRecipe recipe) {
        super(player, recipe);
        setDisplay(loadMenuDisplay(
            VanillaBrewingDisplayConfig.TITLE.value(),
            VanillaBrewingDisplayConfig.LAYOUT.value(),
            VanillaBrewingDisplayConfig.ICONS.value()
        ));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        applyIconType(icon, Map.of(
            VanillaBrewingDisplayIconParser.ICON_TYPE_INGREDIENT,
            itemDisplayIcon -> itemDisplayIcon.setDisplayItem(recipe.ingredient().getItemStack()),
            VanillaBrewingDisplayIconParser.ICON_TYPE_INPUT,
            itemDisplayIcon -> itemDisplayIcon.setDisplayItem(recipe.input().getItemStack())
        ));
    }

    @Override
    public IconParser iconParser() {
        return VanillaBrewingDisplayIconParser.INSTANCE;
    }

}
