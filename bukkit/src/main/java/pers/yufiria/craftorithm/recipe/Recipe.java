package pers.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface Recipe {

    @NotNull NamespacedKey recipeKey();

    @NotNull RecipeResult result();

    int priority();

}
