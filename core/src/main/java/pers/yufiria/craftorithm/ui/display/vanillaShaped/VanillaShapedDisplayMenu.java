package pers.yufiria.craftorithm.ui.display.vanillaShaped;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;

public class VanillaShapedDisplayMenu extends RecipeDisplayMenu<ShapedRecipe> {

    public VanillaShapedDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, ShapedRecipe shapedRecipe) {
        super(player, display, shapedRecipe);
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        switch (icon) {
            case VanillaShapedIngredientIcon vanillaShapedIngredientIcon -> {
                int ingredientSlot = vanillaShapedIngredientIcon.ingredientSlot();
                int row = ingredientSlot / 3;
                int column = ingredientSlot % 3;
                @NotNull String[] shape = recipe.getShape();
                if (row >= shape.length) return;
                String line = shape[row];
                if (column >= line.length()) return;
                char c = line.charAt(column);
                RecipeChoice recipeChoice = recipe.getChoiceMap().get(c);
                if (recipeChoice == null) return;
                vanillaShapedIngredientIcon.setDisplayItem(recipeChoice.getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

}
