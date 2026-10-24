package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.BrewingRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.ingredient.ItemIdRecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.ingredient.RecipeChoiceParser;

public enum BrewingRecipeParser implements RecipeParser<BrewingRecipe> {

    INSTANCE;

    @Override
    public @NotNull RecipeChoiceParser choiceParser() {
        return ItemIdRecipeChoiceParser.INSTANCE;
    }

    @Override
    public @NotNull BrewingRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        if (!RecipeManager.INSTANCE.supportBrewingRecipe()) {
            throw new RecipeLoadException("&cThe server does not support brewing recipes");
        }
        return parseSafely(() -> {
            ItemStack result = requireResult(recipeConfig);
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String inputId = recipeConfig.getString("input");
            RecipeChoice input = choiceParser().parse(inputId);
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = choiceParser().parse(ingredientId);
            return new BrewingRecipe(
                recipeKey,
                input,
                ingredient,
                result
            );
        });
    }
}
