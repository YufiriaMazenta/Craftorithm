package com.github.yufiriamazenta.craftorithm.recipe.loader.vanilla;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.exception.RecipeLoadException;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeLoader;
import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.Objects;

public enum ShapedRecipeLoader implements VanillaRecipeLoader, RecipeLoader<ShapedRecipe> {

    INSTANCE;

    @Override
    public ShapedRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(resultId);
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            ShapedRecipe recipe = new ShapedRecipe(key, result);
            recipe.shape(recipeConfig.getStringList("shape").toArray(new String[0]));
            ConfigurationSection ingredientsConfig = recipeConfig.getConfigurationSection("ingredients");
            for (String ingredientKey : Objects.requireNonNull(ingredientsConfig).getKeys(false)) {
                recipe.setIngredient(ingredientKey.charAt(0), parseChoice(Objects.requireNonNull(ingredientsConfig.getString(ingredientKey))));
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
