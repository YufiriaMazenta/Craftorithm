package com.github.yufiriamazenta.craftorithm.recipe.custom;

import com.github.yufiriamazenta.craftorithm.recipe.SimpleRecipeTypes;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public class PotionMixRecipe implements CustomRecipe {

    private PotionMix potionMix;

    public PotionMixRecipe(PotionMix potionMix) {
        this.potionMix = potionMix;
    }

    public PotionMix potionMix() {
        return potionMix;
    }

    public PotionMixRecipe setPotionMix(PotionMix potionMix) {
        this.potionMix = potionMix;
        return this;
    }

    @Override
    public SimpleRecipeTypes recipeType() {
        return SimpleRecipeTypes.POTION;
    }

    @Override
    public NamespacedKey key() {
        return potionMix.getKey();
    }

    @Override
    public @NotNull ItemStack getResult() {
        return potionMix.getResult();
    }

    public @NotNull RecipeChoice input() {
        return potionMix.getInput();
    }

    public @NotNull RecipeChoice ingredient() {
        return potionMix.getIngredient();
    }

}
