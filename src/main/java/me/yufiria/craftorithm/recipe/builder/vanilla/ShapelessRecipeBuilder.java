package me.yufiria.craftorithm.recipe.builder.vanilla;

import me.yufiria.craftorithm.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;

public class ShapelessRecipeBuilder extends AbstractRecipeBuilder {

    private List<RecipeChoice> choiceList;

    private ShapelessRecipeBuilder() {}

    @Override
    public ShapelessRecipeBuilder key(NamespacedKey key) {
        return (ShapelessRecipeBuilder) super.key(key);
    }

    @Override
    public ShapelessRecipeBuilder result(ItemStack result) {
        return (ShapelessRecipeBuilder) super.result(result);
    }

    public ShapelessRecipeBuilder choiceList(List<RecipeChoice> choiceList) {
        this.choiceList = choiceList;
        return this;
    }

    public List<RecipeChoice> getChoiceList() {
        return choiceList;
    }

    public ShapelessRecipe build() {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(getKey(), getResult());
        for (RecipeChoice choice : choiceList) {
            shapelessRecipe.addIngredient(choice);
        }
        return shapelessRecipe;
    }

    public static ShapelessRecipeBuilder builder() {
        return new ShapelessRecipeBuilder();
    }

}
