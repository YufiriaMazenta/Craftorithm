package com.github.yufiriamazenta.craftorithm.recipe.loader.vanilla;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.util.RecipeChoiceParser;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeLoader;
import com.github.yufiriamazenta.craftorithm.recipe.exception.RecipeLoadException;
import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.List;
import java.util.Objects;

public enum ShapelessRecipeLoader implements RecipeLoader<ShapelessRecipe> {

    INSTANCE;

    @Override
    public ShapelessRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(resultId);
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            ShapelessRecipe recipe = new ShapelessRecipe(key, result);
            List<String> ingredientKeys = recipeConfig.getStringList("ingredients");
            if (ingredientKeys.isEmpty()) {
                throw new RecipeLoadException("No ingredients found for " + recipeKey);
            }
            for (String ingredientKey : ingredientKeys) {
                recipe.addIngredient(RecipeChoiceParser.parseChoice(ingredientKey));
            }
            String group = recipeConfig.getString("group");
            if (group != null) {
                recipe.setGroup(group);
            }
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_19_3)) {
                if (recipeConfig.contains("recipe_book_category")) {
                    String categoryStr = Objects.requireNonNull(recipeConfig.getString("recipe_book_category")).toUpperCase();
                    CraftingBookCategory category = CraftingBookCategory.valueOf(categoryStr);
                    recipe.setCategory(category);
                }
            }
            return recipe;
        } catch (Throwable e) {
            throw new RecipeLoadException(e);
        }
    }
}
