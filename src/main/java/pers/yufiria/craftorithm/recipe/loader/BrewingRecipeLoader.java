package pers.yufiria.craftorithm.recipe.loader;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.extra.brewing.BrewingRecipe;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;

public enum BrewingRecipeLoader implements RecipeLoader<BrewingRecipe> {

    INSTANCE;

    @Override
    public @NotNull BrewingRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId));
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            String inputId = recipeConfig.getString("input");
            RecipeChoice input = BukkitRecipeChoiceParser.parseChoice(inputId);
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = BukkitRecipeChoiceParser.parseChoice(ingredientId);
            PotionMix potionMix = new PotionMix(key, result, input, ingredient);
            return new BrewingRecipe(potionMix);
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }
}
