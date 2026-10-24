package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.ingredient.ItemIdRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.ingredient.RecipeChoiceParser;

public interface VanillaRecipeParser<R extends Recipe> extends RecipeParser<R> {

    @Override
    default @NotNull RecipeChoiceParser choiceParser() {
        return ItemIdRecipeChoiceParser.INSTANCE;
    }
}
