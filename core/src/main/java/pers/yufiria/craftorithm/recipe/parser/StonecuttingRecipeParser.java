package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;

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
        return parseSafely(() -> {
            ItemStack result = requireResult(recipeConfig);
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = choiceParser().parse(ingredientId);
            if (supportPredicateChoice) {
                ingredient = RecipeChoice.predicateChoice(ingredient, ingredient.getItemStack());
            }
            StonecuttingRecipe recipe = new StonecuttingRecipe(recipeKey, result, ingredient);
            RecipeMetaUtils.applyGroup(recipe, recipeConfig);
            return recipe;
        });
    }
}
