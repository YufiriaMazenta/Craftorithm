package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RandomCookingRecipeRegistry extends CookingRecipeRegistry {


    public RandomCookingRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(result(), "Recipe key cannot be null");
        Objects.requireNonNull(source(), "Recipe ingredient cannot be null");
        CookingRecipe<?> cookingRecipe;
        switch (cookingBlock()) {
            case FURNACE:
            default:
                cookingRecipe = new FurnaceRecipe(namespacedKey(), result(), source(), exp(), time());
                break;
            case SMOKER:
                cookingRecipe = new SmokingRecipe(namespacedKey(), result(), source(), exp(), time());
                break;
            case BLAST_FURNACE:
                cookingRecipe = new BlastingRecipe(namespacedKey(), result(), source(), exp(), time());
                break;
            case CAMPFIRE:
                cookingRecipe = new CampfireRecipe(namespacedKey(), result(), source(), exp(), time());
                break;
        }
        cookingRecipe.setGroup(group());
        RecipeManager.INSTANCE.regRecipe(group(), cookingRecipe, RecipeType.RANDOM_COOKING);
    }
}
