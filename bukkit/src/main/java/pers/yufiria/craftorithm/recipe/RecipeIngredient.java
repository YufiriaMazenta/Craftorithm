package pers.yufiria.craftorithm.recipe;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.StackedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemId;

import java.util.List;

public class RecipeIngredient {

    private List<StackedItemId> ingredients;

    public RecipeIngredient(@NotNull NamespacedItemId ingredientItemId, @NotNull Integer amount) {

    }

    public boolean match(StackedItemId itemId) {
        for (StackedItemId ingredient : ingredients) {
            if (itemId.itemId().equals(ingredient.itemId()) && itemId.amount().equals(ingredient.amount())) {
                return true;
            }
        }
        return false;
    }

}
