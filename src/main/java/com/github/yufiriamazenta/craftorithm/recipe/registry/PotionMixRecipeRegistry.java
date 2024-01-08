package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PotionMixRecipeRegistry extends RecipeRegistry {

    private RecipeChoice input, ingredient;

    public PotionMixRecipeRegistry(@NotNull String recipeGroup, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(recipeGroup, namespacedKey, result);
    }

    public RecipeChoice input() {
        return input;
    }

    public PotionMixRecipeRegistry setInput(RecipeChoice input) {
        this.input = input;
        return this;
    }

    public RecipeChoice ingredient() {
        return ingredient;
    }

    public PotionMixRecipeRegistry setIngredient(RecipeChoice ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey, "Recipe key cannot be null");
        Objects.requireNonNull(result, "Recipe result cannot be null");
        Objects.requireNonNull(ingredient, "Recipe ingredient cannot be null");
        Objects.requireNonNull(input, "Recipe input cannot be null");

        PotionMixRecipe potionMixRecipe = new PotionMixRecipe(new PotionMix(namespacedKey, result, input, ingredient));
        RecipeManager.INSTANCE.regRecipe(group, potionMixRecipe, RecipeType.POTION);
    }
}
