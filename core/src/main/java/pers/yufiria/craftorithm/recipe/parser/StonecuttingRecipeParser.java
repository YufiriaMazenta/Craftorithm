package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.Objects;

public enum StonecuttingRecipeParser implements VanillaRecipeParser<StonecuttingRecipe> {

    INSTANCE;
    private final boolean supportPredicateChoice;

    StonecuttingRecipeParser() {
        boolean supportPredicateChoice;
        try {
            Class.forName("org.bukkit.inventory.RecipeChoice$PredicateChoice");
            supportPredicateChoice = true;
        } catch (ClassNotFoundException e) {
            supportPredicateChoice = false;
        }
        this.supportPredicateChoice = supportPredicateChoice;
    }

    @Override
    public @NotNull StonecuttingRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId)).orElseThrow(() -> new ItemNotFoundException("Item not found: " + resultId));
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = choiceParser().parse(ingredientId);
            if (supportPredicateChoice) {
                ingredient = RecipeChoice.predicateChoice(ingredient, ingredient.getItemStack());
            }
            StonecuttingRecipe recipe = new StonecuttingRecipe(recipeKey, result, ingredient);
            if (recipeConfig.contains("group")) {
                recipe.setGroup(Objects.requireNonNull(recipeConfig.getString("group")));
            }
            return recipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }

    }
}
