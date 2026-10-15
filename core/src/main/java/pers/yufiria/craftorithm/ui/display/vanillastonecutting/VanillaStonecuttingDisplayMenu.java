package pers.yufiria.craftorithm.ui.display.vanillastonecutting;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaStonecuttingDisplayConfig;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;

public class VanillaStonecuttingDisplayMenu extends RecipeDisplayMenu<StonecuttingRecipe> {

    public VanillaStonecuttingDisplayMenu(@NotNull Player player, StonecuttingRecipe recipe) {
        super(player, recipe);
        setDisplay(
            loadMenuDisplay(
                VanillaStonecuttingDisplayConfig.TITLE.value(),
                VanillaStonecuttingDisplayConfig.LAYOUT.value(),
                VanillaStonecuttingDisplayConfig.ICONS.value()
            )
        );
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        applyIconType(icon, Map.of(
            VanillaStonecuttingDisplayIconParser.ICON_TYPE_INGREDIENT,
            itemDisplayIcon -> itemDisplayIcon.setDisplayItem(recipe.getInputChoice().getItemStack())
        ));
    }

    @Override
    public IconParser iconParser() {
        return VanillaStonecuttingDisplayIconParser.INSTANCE;
    }

}
