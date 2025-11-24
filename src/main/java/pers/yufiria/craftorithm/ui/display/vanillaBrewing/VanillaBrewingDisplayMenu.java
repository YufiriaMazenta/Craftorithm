package pers.yufiria.craftorithm.ui.display.vanillaBrewing;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;

public class VanillaBrewingDisplayMenu extends RecipeDisplayMenu<BrewingRecipe> {

    public VanillaBrewingDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, BrewingRecipe recipe) {
        super(player, display, recipe);
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        switch (icon) {
            case VanillaBrewingIngredientIcon ingredientIcon -> {
                ingredientIcon.setDisplayItem(recipe.ingredient().getItemStack());
            }
            case VanillaBrewingInputIcon inputIcon -> {
                inputIcon.setDisplayItem(recipe.input().getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

}
