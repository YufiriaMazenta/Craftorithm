package pers.yufiria.craftorithm.ui.display.vanillaBrewing;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaBrewingDisplay;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

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

    @Override
    public IconParser iconParser() {
        return VanillaBrewingDisplayIconParser.INSTANCE;
    }

}
