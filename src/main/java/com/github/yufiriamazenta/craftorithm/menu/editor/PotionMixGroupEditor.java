package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PotionMixGroupEditor extends RecipeGroupEditor {

    public PotionMixGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player, recipeGroup);
        setDisplay(
            new MenuDisplay(
                title,
                new MenuLayout(
                    //TODO
                )
            )
        );
    }

}
