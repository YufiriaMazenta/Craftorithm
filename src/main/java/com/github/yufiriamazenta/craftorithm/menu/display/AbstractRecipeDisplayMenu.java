package com.github.yufiriamazenta.craftorithm.menu.display;

import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRecipeDisplayMenu extends Menu {

    protected Recipe recipe;

    public AbstractRecipeDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, @NotNull Recipe recipe) {
        super(player, display);
        this.recipe = recipe;
    }

}
