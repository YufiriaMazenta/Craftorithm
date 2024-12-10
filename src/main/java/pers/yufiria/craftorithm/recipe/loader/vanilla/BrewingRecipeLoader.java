package pers.yufiria.craftorithm.recipe.loader.vanilla;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.BrewingRecipe;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.RecipeChoiceParser;

public enum BrewingRecipeLoader implements RecipeLoader<BrewingRecipe> {

    INSTANCE;

    @Override
    public @NotNull BrewingRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(resultId);
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            String inputId = recipeConfig.getString("input");
            RecipeChoice input = RecipeChoiceParser.parseChoice(inputId);
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = RecipeChoiceParser.parseChoice(ingredientId);
            PotionMix potionMix = new PotionMix(key, result, input, ingredient);
            return new BrewingRecipe(potionMix);
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }
}
