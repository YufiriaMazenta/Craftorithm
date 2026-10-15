package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.Objects;

public enum ShapedRecipeParser implements VanillaRecipeParser<ShapedRecipe> {

    INSTANCE;

    @Override
    public ShapedRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        return parseSafely(() -> {
            ItemStack result = requireResult(recipeConfig);
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
            recipe.shape(recipeConfig.getStringList("shape").toArray(new String[0]));
            ConfigurationSection ingredientsConfig = recipeConfig.getConfigurationSection("ingredients");
            for (String ingredientKey : Objects.requireNonNull(ingredientsConfig).getKeys(false)) {
                recipe.setIngredient(ingredientKey.charAt(0), choiceParser().parse(Objects.requireNonNull(ingredientsConfig.getString(ingredientKey))));
            }
            RecipeMetaUtils.applyCraftingMeta(recipe, recipeConfig);
            return recipe;
        });
    }

}
