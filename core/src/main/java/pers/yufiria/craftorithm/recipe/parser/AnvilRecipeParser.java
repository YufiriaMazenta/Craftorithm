package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.choice.ItemIdStackRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.RecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

public enum AnvilRecipeParser implements RecipeParser<AnvilRecipe> {

    INSTANCE;

    @Override
    public @NotNull RecipeChoiceParser choiceParser() {
        return ItemIdStackRecipeChoiceParser.INSTANCE;
    }

    @Override
    public @NotNull AnvilRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String resultId = recipeConfig.getString("result");
            NamespacedItemIdStack result = NamespacedItemIdStack.fromString(resultId);
            String baseId = recipeConfig.getString("base");
            StackableItemIdChoice base = (StackableItemIdChoice) choiceParser().parse(baseId);
            String additionId = recipeConfig.getString("addition");
            StackableItemIdChoice addition = (StackableItemIdChoice) choiceParser().parse(additionId);
            int costLevel = recipeConfig.getInt("cost_level", 0);
            AnvilRecipe anvilRecipe = new AnvilRecipe(recipeKey, result, base, addition);
            anvilRecipe.setCostLevel(costLevel);
            return anvilRecipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
