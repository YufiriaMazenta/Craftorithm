package pers.yufiria.craftorithm.ui.display.vanillaSmelting;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.Map;

public class VanillaSmeltingDisplayMenu extends RecipeDisplayMenu<CookingRecipe<?>> {

    public VanillaSmeltingDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, CookingRecipe<?> recipe) {
        super(player, display, recipe);
    }

    @Override
    public String parsedMenuTitle() {
        return replaceExpAndTime(super.parsedMenuTitle());
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ActionIcon actionIcon) {
            actionIcon.setTextReplaceMap(Map.of("<reward_exp>", recipe.getExperience() + "", "<time>", recipe.getCookingTime() + ""));
        }
        switch (icon) {
            case VanillaSmeltingIngredientIcon ingredientIcon -> {
                ingredientIcon.setDisplayItem(recipe.getInputChoice().getItemStack());
            }
            case RecipeResultIcon resultIcon -> {
                resultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

    public String replaceExpAndTime(String origin) {
        return origin.replace("<reward_exp>", recipe.getExperience() + "").replace("<time>", recipe.getCookingTime() + "");
    }

}
