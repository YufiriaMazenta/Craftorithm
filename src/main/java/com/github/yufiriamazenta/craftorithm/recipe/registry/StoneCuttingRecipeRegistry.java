package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StoneCuttingRecipeRegistry extends UnlockableRecipeRegistry {

    protected RecipeChoice ingredient;

    public StoneCuttingRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey, "Recipe key cannot be null");
        Objects.requireNonNull(result, "Recipe result cannot be null");
        Objects.requireNonNull(ingredient, "Recipe ingredient cannot be null");
        Recipe stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, ingredient);
        RecipeManager.INSTANCE.regRecipe(group(), stonecuttingRecipe, RecipeType.STONE_CUTTING);
        RecipeManager.INSTANCE.recipeUnlockMap().put(namespacedKey, unlock);
    }

    public RecipeChoice ingredient() {
        return ingredient;
    }

    public StoneCuttingRecipeRegistry setIngredient(RecipeChoice ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    @Override
    public RecipeType recipeType() {
        return RecipeType.STONE_CUTTING;
    }

}
