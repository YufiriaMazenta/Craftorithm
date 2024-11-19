package com.github.yufiriamazenta.craftorithm.recipe.vanilla;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.ConfigRecipeLoader;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeLoadException;
import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.Objects;

public enum ShapedRecipeLoader implements ConfigRecipeLoader<ShapedRecipe>, VanillaRecipeLoader {

    INSTANCE;

    @Override
    public ShapedRecipe loadRecipe(String recipeKey, ConfigurationSection recipeSource) {
        try {
            String resultId = recipeSource.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(resultId);
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            ShapedRecipe recipe = new ShapedRecipe(key, result);
            recipe.shape(recipeSource.getStringList("shape").toArray(new String[0]));
            ConfigurationSection ingredientsConfig = recipeSource.getConfigurationSection("ingredients");
            for (String ingredientKey : Objects.requireNonNull(ingredientsConfig).getKeys(false)) {
                recipe.setIngredient(ingredientKey.charAt(0), parseChoice(Objects.requireNonNull(ingredientsConfig.getString(ingredientKey))));
            }
            String group = recipeSource.getString("group");
            if (group != null) {
                recipe.setGroup(group);
            }
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_19_3)) {
                if (recipeSource.contains("recipe_book_category")) {
                    String categoryStr = Objects.requireNonNull(recipeSource.getString("recipe_book_category")).toUpperCase();
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
