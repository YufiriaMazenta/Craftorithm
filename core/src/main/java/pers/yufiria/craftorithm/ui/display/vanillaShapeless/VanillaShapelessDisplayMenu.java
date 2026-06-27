package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.List;

public class VanillaShapelessDisplayMenu extends RecipeDisplayMenu<ShapelessRecipe> {

    public VanillaShapelessDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, ShapelessRecipe recipe) {
        super(player, display, recipe);
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        switch (icon) {
            case VanillaShapelessIngredientIcon vanillaShapelessIngredientIcon -> {
                int ingredientSlot = vanillaShapelessIngredientIcon.ingredientSlot();
                List<RecipeChoice> choiceList = recipe.getChoiceList();
                if (ingredientSlot >= choiceList.size()) {
                    return;
                }
                RecipeChoice recipeChoice = choiceList.get(ingredientSlot);
                if (recipeChoice != null) {
                    vanillaShapelessIngredientIcon.setDisplayItem(recipeChoice.getItemStack());
                } else {
                    vanillaShapelessIngredientIcon.setDisplayItem(new ItemStack(Material.AIR));
                }
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

}
