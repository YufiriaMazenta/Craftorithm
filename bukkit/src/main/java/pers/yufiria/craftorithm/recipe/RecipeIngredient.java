package pers.yufiria.craftorithm.recipe;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public class RecipeIngredient {

    private final @NotNull NamespacedItemId ingredientItemId;
    private final @NotNull Integer amount;

    public RecipeIngredient(@NotNull NamespacedItemId ingredientItemId, @NotNull Integer amount) {
        this.ingredientItemId = ingredientItemId;
        this.amount = amount;
    }

    public @NotNull NamespacedItemId ingredientItemId() {
        return ingredientItemId;
    }

    public @NotNull Integer amount() {
        return amount;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeIngredient that)) return false;

        return toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return ingredientItemId + " " + amount;
    }

}
