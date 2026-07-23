package pers.yufiria.craftorithm.recipe;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public class BrewingRecipe implements CustomRecipe {

    private NamespacedKey recipeKey;
    private RecipeChoice input, ingredient;
    private ItemStack result;

    public BrewingRecipe(NamespacedKey recipeKey, RecipeChoice input, RecipeChoice ingredient, ItemStack result) {
        this.recipeKey = recipeKey;
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
    }

    public PotionMix toPotionMix() {
        return new PotionMix(
            recipeKey,
            result,
            PotionMix.createPredicateChoice(input),
            PotionMix.createPredicateChoice(ingredient)
        );
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return recipeKey;
    }

    @Override
    public @NotNull ItemStack getResult() {
        return result;
    }

    public @NotNull RecipeChoice input() {
        return input;
    }

    public @NotNull RecipeChoice ingredient() {
        return ingredient;
    }

}
