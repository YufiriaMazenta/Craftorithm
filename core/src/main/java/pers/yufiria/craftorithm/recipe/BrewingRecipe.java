package pers.yufiria.craftorithm.recipe;

import crypticlib.MinecraftVersion;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.util.IngredientUtils;

@SuppressWarnings({"removal", "deprecation"})
public class BrewingRecipe implements CustomRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice input, ingredient;
    private final ItemStack result;

    public BrewingRecipe(NamespacedKey recipeKey, RecipeChoice input, RecipeChoice ingredient, ItemStack result) {
        this.recipeKey = recipeKey;
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
    }

    public Object toPotionMix() {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_3)) {
            //支持predicate
            return new PotionMix(
                recipeKey,
                result,
                PotionMix.createPredicateChoice(input),
                PotionMix.createPredicateChoice(ingredient)
            );
        } else {
            //版本太低了，不支持predicate
            return new PotionMix(
                recipeKey,
                result,
                IngredientUtils.getBukkitChoice(input),
                IngredientUtils.getBukkitChoice(ingredient)
            );
        }
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

    public static BrewingRecipe fromPaperBrewingRecipe(Recipe recipe) {
        if (MinecraftVersion.current().before(MinecraftVersion.V26_3)) {
            return null;
        }
        if (!(recipe instanceof org.bukkit.inventory.BrewingRecipe brewingRecipe)) {
            return null;
        }
        return new BrewingRecipe(
            brewingRecipe.getKey(),
            brewingRecipe.getInput(),
            brewingRecipe.getIngredient(),
            brewingRecipe.getResult()
        );
    }

}
