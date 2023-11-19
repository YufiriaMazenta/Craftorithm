package com.github.yufiriamazenta.craftorithm.recipe.builder.vanilla;

import com.github.yufiriamazenta.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;

public class ShapelessRecipeBuilder extends AbstractRecipeBuilder {

    private List<RecipeChoice> choiceList;

    private ShapelessRecipeBuilder() {}

    @Override
    public ShapelessRecipeBuilder setKey(NamespacedKey key) {
        return (ShapelessRecipeBuilder) super.setKey(key);
    }

    @Override
    public ShapelessRecipeBuilder setResult(ItemStack result) {
        return (ShapelessRecipeBuilder) super.setResult(result);
    }

    public ShapelessRecipeBuilder choiceList(List<RecipeChoice> choiceList) {
        this.choiceList = choiceList;
        return this;
    }

    public List<RecipeChoice> getChoiceList() {
        return choiceList;
    }

    public ShapelessRecipe build() {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key(), result());
        for (RecipeChoice choice : choiceList) {
            shapelessRecipe.addIngredient(choice);
        }
        return shapelessRecipe;
    }

    public static ShapelessRecipeBuilder builder() {
        return new ShapelessRecipeBuilder();
    }

}
