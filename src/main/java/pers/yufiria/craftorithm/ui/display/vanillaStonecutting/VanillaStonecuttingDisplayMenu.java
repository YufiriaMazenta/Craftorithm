package pers.yufiria.craftorithm.ui.display.vanillaStonecutting;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

public class VanillaStonecuttingDisplayMenu extends RecipeDisplayMenu<StonecuttingRecipe> {

    public VanillaStonecuttingDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, StonecuttingRecipe recipe) {
        super(player, display, recipe);
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

}
