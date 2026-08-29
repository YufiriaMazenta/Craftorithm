package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.Objects;

public enum ShapedRecipeParser implements VanillaRecipeParser<ShapedRecipe> {

    INSTANCE;

    @Override
    public ShapedRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId)).orElseThrow();
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
            recipe.shape(recipeConfig.getStringList("shape").toArray(new String[0]));
            ConfigurationSection ingredientsConfig = recipeConfig.getConfigurationSection("ingredients");
            for (String ingredientKey : Objects.requireNonNull(ingredientsConfig).getKeys(false)) {
                recipe.setIngredient(ingredientKey.charAt(0), choiceParser().parse(Objects.requireNonNull(ingredientsConfig.getString(ingredientKey))));
            }
            String group = recipeConfig.getString("group");
            if (group != null) {
                recipe.setGroup(group);
            }
            if (recipeConfig.contains("recipe_book_category")) {
                String categoryStr = Objects.requireNonNull(recipeConfig.getString("recipe_book_category")).toUpperCase();
                CraftingBookCategory category = CraftingBookCategory.valueOf(categoryStr);
                recipe.setCategory(category);
            }
            return recipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable e) {
            throw new RecipeLoadException(e);
        }
    }

}
