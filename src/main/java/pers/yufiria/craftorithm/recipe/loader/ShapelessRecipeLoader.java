package pers.yufiria.craftorithm.recipe.loader;

import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;

import java.util.List;
import java.util.Objects;

public enum ShapelessRecipeLoader implements RecipeLoader<ShapelessRecipe> {

    INSTANCE;

    @Override
    public ShapelessRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId));
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            ShapelessRecipe recipe = new ShapelessRecipe(key, result);
            List<String> ingredientKeys = recipeConfig.getStringList("ingredients");
            if (ingredientKeys.isEmpty()) {
                throw new RecipeLoadException("No ingredients found for " + recipeKey);
            }
            for (String ingredientKey : ingredientKeys) {
                recipe.addIngredient(BukkitRecipeChoiceParser.parseChoice(ingredientKey));
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
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable e) {
            throw new RecipeLoadException(e);
        }
    }
}
