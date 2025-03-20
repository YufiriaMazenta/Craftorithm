package pers.yufiria.craftorithm.ui.vanillaShapeless;

import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class VanillaShapelessDisplayMenu extends Menu {

    private final ShapelessRecipe recipe;

    public VanillaShapelessDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, ShapelessRecipe recipe) {
        super(player, display);
        this.recipe = recipe;
    }



}
