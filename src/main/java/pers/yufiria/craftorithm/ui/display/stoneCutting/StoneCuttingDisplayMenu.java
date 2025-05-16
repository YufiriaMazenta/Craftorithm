package pers.yufiria.craftorithm.ui.display.stoneCutting;

import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

public class StoneCuttingDisplayMenu extends Menu {

    private final StonecuttingRecipe recipe;

    public StoneCuttingDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, StonecuttingRecipe recipe) {
        super(player, display);
        this.recipe = recipe;
    }

}
