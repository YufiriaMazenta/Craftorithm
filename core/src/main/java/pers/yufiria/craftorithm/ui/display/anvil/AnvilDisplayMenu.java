package pers.yufiria.craftorithm.ui.display.anvil;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.AnvilDisplay;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;

public class AnvilDisplayMenu extends RecipeDisplayMenu<AnvilRecipe> {

    public AnvilDisplayMenu(@NotNull Player player, AnvilRecipe anvilRecipe) {
        super(
            player,
            anvilRecipe
        );
        setDisplay(loadMenuDisplay(
            AnvilDisplay.TITLE.value(),
            AnvilDisplay.LAYOUT.value(),
            AnvilDisplay.ICONS.value()
        ));
    }

    @Override
    public String parsedMenuTitle() {
        return replaceCostLevel(super.parsedMenuTitle());
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ActionIcon actionIcon) {
            actionIcon.setTextReplaceMap(Map.of("<level>", recipe.costLevel() + ""));
        }
        switch (icon) {
            case AnvilBaseIcon anvilBaseIcon -> {
                anvilBaseIcon.setDisplayItem(recipe.base().getItemStack());
            }
            case AnvilAdditionIcon anvilAdditionIcon -> {
                anvilAdditionIcon.setDisplayItem(recipe.addition().getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

    private String replaceCostLevel(String originText) {
        return originText.replace("<level>", recipe.costLevel() + "");
    }

    @Override
    public IconParser iconParser() {
        return AnvilDisplayIconParser.INSTANCE;
    }

}
