package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeParser;
import pers.yufiria.craftorithm.recipe.choice.BukkitRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.ItemIdRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.RecipeChoiceParser;

public interface VanillaRecipeParser<R extends Recipe> extends RecipeParser<R> {

    @Override
    default @NotNull RecipeChoiceParser choiceParser() {
        if (PluginConfigs.USE_EXPERIMENTAL_RECIPE_REGISTER.value()) {
            return ItemIdRecipeChoiceParser.INSTANCE;
        } else {
            return BukkitRecipeChoiceParser.INSTANCE;
        }
    }
}
