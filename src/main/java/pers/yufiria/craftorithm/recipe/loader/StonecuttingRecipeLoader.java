package pers.yufiria.craftorithm.recipe.loader;

import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum StonecuttingRecipeLoader implements RecipeLoader<StonecuttingRecipe> {

    INSTANCE;

    @Override
    public @NotNull StonecuttingRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId));
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = BukkitRecipeChoiceParser.parseChoice(ingredientId);
            StonecuttingRecipe recipe = new StonecuttingRecipe(key, result, ingredient);
            if (recipeConfig.contains("group")) {
                recipe.setGroup(Objects.requireNonNull(recipeConfig.getString("group")));
            }
            return recipe;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }

    }
}
