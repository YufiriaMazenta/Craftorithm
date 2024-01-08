package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class StoneCuttingRecipeRegistry extends UnlockableRecipeRegistry {

    private List<ItemStack> results;
    private List<RecipeChoice> ingredientList = new CopyOnWriteArrayList<>();
    private List<NamespacedKey> generateRecipeKeys = new CopyOnWriteArrayList<>();

    public StoneCuttingRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull List<ItemStack> results) {
        super(group, namespacedKey, null);
        this.results = results;
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey, "Recipe key cannot be null");
        Objects.requireNonNull(result, "Recipe result cannot be null");
        Objects.requireNonNull(source, "Recipe ingredient cannot be null");
        StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, source);
        stonecuttingRecipe.setGroup(group);
        RecipeManager.INSTANCE.regRecipe(group(), stonecuttingRecipe, RecipeType.STONE_CUTTING);
        RecipeManager.INSTANCE.recipeUnlockMap().put(namespacedKey, unlock);
    }

    public List<RecipeChoice> ingredientList() {
        return ingredientList;
    }

    public StoneCuttingRecipeRegistry setIngredientList(List<RecipeChoice> ingredientList) {
        this.ingredientList = ingredientList;
        return this;
    }

    public List<ItemStack> results() {
        return results;
    }

    public StoneCuttingRecipeRegistry setResults(List<ItemStack> results) {
        this.results = results;
        return this;
    }

    public List<NamespacedKey> generateRecipeKeys() {
        return generateRecipeKeys;
    }

}
