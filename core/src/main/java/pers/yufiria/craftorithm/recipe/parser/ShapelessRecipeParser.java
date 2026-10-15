package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.List;

public enum ShapelessRecipeParser implements VanillaRecipeParser<ShapelessRecipe> {

    INSTANCE;

    @Override
    public ShapelessRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        return parseSafely(() -> {
            ItemStack result = requireResult(recipeConfig);
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            ShapelessRecipe recipe = new ShapelessRecipe(recipeKey, result);
            List<String> ingredientKeys = recipeConfig.getStringList("ingredients");
            if (ingredientKeys.isEmpty()) {
                throw new RecipeLoadException("No ingredients found for " + recipeName);
            }
            for (String ingredientKey : ingredientKeys) {
                recipe.addIngredient(choiceParser().parse(ingredientKey));
            }
            RecipeMetaUtils.applyCraftingMeta(recipe, recipeConfig);
            return recipe;
        });
    }
}
