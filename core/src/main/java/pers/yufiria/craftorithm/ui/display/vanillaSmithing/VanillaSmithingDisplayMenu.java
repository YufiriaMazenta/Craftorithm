package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.MinecraftVersion;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;

public class VanillaSmithingDisplayMenu extends RecipeDisplayMenu<SmithingRecipe> {

    public VanillaSmithingDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, SmithingRecipe recipe) {
        super(player, display, recipe);
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        switch (icon) {
            case VanillaSmithingTemplateIcon templateIcon -> {
                if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20)) {
                    if (recipe instanceof SmithingTransformRecipe transformRecipe) {
                        templateIcon.setDisplayItem(transformRecipe.getTemplate().getItemStack());
                    } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
                        templateIcon.setDisplayItem(trimRecipe.getTemplate().getItemStack());
                    } else {
                        templateIcon.setDisplayItem(null);
                    }
                } else {
                    templateIcon.setDisplayItem(null);
                }
            }
            case VanillaSmithingBaseIcon vanillaSmithingBaseIcon -> {
                vanillaSmithingBaseIcon.setDisplayItem(recipe.getBase().getItemStack());
            }
            case VanillaSmithingAdditionIcon vanillaSmithingAdditionIcon -> {
                vanillaSmithingAdditionIcon.setDisplayItem(recipe.getAddition().getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

}
