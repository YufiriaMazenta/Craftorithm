package com.github.yufiriamazenta.craftorithm.menu.editor;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import crypticlib.ui.menu.Menu;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RecipeGroupEditor extends Menu {

    private final RecipeGroup recipeGroup;

    public RecipeGroupEditor(@NotNull Player player, @NotNull RecipeGroup recipeGroup) {
        super(player);
        this.recipeGroup = recipeGroup;
        Validate.notNull(recipeGroup);
    }

    //TODO 配方组编辑

}
