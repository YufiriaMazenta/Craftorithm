package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.choice.ItemIdRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.RecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessorManager;

public enum BrewingRecipeParser implements RecipeParser<BrewingRecipe> {

    INSTANCE;

    @Override
    public @NotNull RecipeChoiceParser choiceParser() {
        return ItemIdRecipeChoiceParser.INSTANCE;
    }

    @Override
    public @NotNull BrewingRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            throw new RecipeLoadException("&cThe server does not support brewing recipes");
        }
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId)).orElseThrow();
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String inputId = recipeConfig.getString("input");
            RecipeChoice input = choiceParser().parse(inputId);
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = choiceParser().parse(ingredientId);
            if (recipeConfig.isConfigurationSection("result_processors")) {
                ConfigurationSection section = recipeConfig.getConfigurationSection("result_processors");
                ResultProcessorManager.INSTANCE.addRecipeProcessors(recipeKey, section);
            }
            return new BrewingRecipe(
                recipeKey,
                input,
                ingredient,
                result
            );
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }
}
