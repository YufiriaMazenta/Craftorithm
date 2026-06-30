package pers.yufiria.craftorithm.ui.display.vanillaStonecutting;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaStonecuttingDisplay;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

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
        switch (icon) {
            case VanillaStonecuttingIngredientIcon ingredientIcon -> {
                ingredientIcon.setDisplayItem(recipe.getInputChoice().getItemStack());
            }
            case RecipeResultIcon resultIcon -> {
                resultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

    @Override
    public IconParser iconParser() {
        return VanillaStonecuttingDisplayIconParser.INSTANCE;
    }

}
