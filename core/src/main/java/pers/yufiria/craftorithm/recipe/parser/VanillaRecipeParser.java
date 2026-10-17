package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.choice.BukkitRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.ItemIdRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.RecipeChoiceParser;

public interface VanillaRecipeParser<R extends Recipe> extends RecipeParser<R> {

    @Override
    default @NotNull RecipeChoiceParser choiceParser() {
        return ItemIdRecipeChoiceParser.INSTANCE;
    }
}
