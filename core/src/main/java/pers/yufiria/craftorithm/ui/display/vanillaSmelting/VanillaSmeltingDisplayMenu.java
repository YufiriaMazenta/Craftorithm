package pers.yufiria.craftorithm.ui.display.vanillaSmelting;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingBlastDisplay;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingCampfireDisplay;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingFurnaceDisplay;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingSmokerDisplay;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;

public class VanillaSmeltingDisplayMenu extends RecipeDisplayMenu<CookingRecipe<?>> {

    public VanillaSmeltingDisplayMenu(@NotNull Player player, CookingRecipe<?> recipe) {
        super(player, recipe);
        switch (recipe) {
            case FurnaceRecipe furnaceRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingFurnaceDisplay.TITLE.value(),
                        VanillaSmeltingFurnaceDisplay.LAYOUT.value(),
                        VanillaSmeltingFurnaceDisplay.ICONS.value()
                    )
                );
            }
            case BlastingRecipe blastingRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingBlastDisplay.TITLE.value(),
                        VanillaSmeltingBlastDisplay.LAYOUT.value(),
                        VanillaSmeltingBlastDisplay.ICONS.value()
                    )
                );
            }
            case SmokingRecipe smokingRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingSmokerDisplay.TITLE.value(),
                        VanillaSmeltingSmokerDisplay.LAYOUT.value(),
                        VanillaSmeltingSmokerDisplay.ICONS.value()
                    )
                );
            }
            case CampfireRecipe campfireRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingCampfireDisplay.TITLE.value(),
                        VanillaSmeltingCampfireDisplay.LAYOUT.value(),
                        VanillaSmeltingCampfireDisplay.ICONS.value()
                    )
                );
            }
            default -> throw new IllegalStateException("Unexpected value: " + recipe);
        }
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

    @Override
    public IconParser iconParser() {
        return VanillaSmeltingDisplayIconParser.INSTANCE;
    }
}
