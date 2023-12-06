package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.DefRecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ShapelessRecipeRegistry extends RecipeRegistry {

    private List<RecipeChoice> choiceList;

    public ShapelessRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
    }

    public List<RecipeChoice> choiceList() {
        return choiceList;
    }

    public ShapelessRecipeRegistry setChoiceList(List<RecipeChoice> choiceList) {
        this.choiceList = choiceList;
        return this;
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(result(), "Recipe key cannot be null");
        Objects.requireNonNull(choiceList, "Recipe ingredients cannot be null");
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey(), result());
        for (RecipeChoice choice : choiceList) {
            shapelessRecipe.addIngredient(choice);
        }
        shapelessRecipe.setGroup(group());
        DefRecipeManager.INSTANCE.regRecipe(group(), shapelessRecipe, RecipeType.SHAPELESS);
    }
}
