package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RandomCookingRecipeRegistry extends CookingRecipeRegistry {

    private List<RandomCookingResult> results;

    //TODO 存抽奖结果
    public RandomCookingRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
    }

    @Override
    public void register() {
        CookingRecipe<?> cookingRecipe = generateCookingRecipe();
        RecipeManager.INSTANCE.regRecipe(group, cookingRecipe, RecipeType.RANDOM_COOKING);
        RecipeManager.INSTANCE.recipeUnlockMap().put(namespacedKey, unlock);
    }

    public List<RandomCookingResult> results() {
        return results;
    }

    public RandomCookingRecipeRegistry setResults(List<RandomCookingResult> results) {
        this.results = results;
        return this;
    }

    public static class RandomCookingResult {
        private ItemStack result;
        private int weight;

        public RandomCookingResult(ItemStack result, int weight) {
            this.result = result;
            this.weight = weight;
        }

        public ItemStack result() {
            return result;
        }

        public RandomCookingResult setResult(ItemStack result) {
            this.result = result;
            return this;
        }

        public int weight() {
            return weight;
        }

        public RandomCookingResult setWeight(int weight) {
            this.weight = weight;
            return this;
        }
    }

}
