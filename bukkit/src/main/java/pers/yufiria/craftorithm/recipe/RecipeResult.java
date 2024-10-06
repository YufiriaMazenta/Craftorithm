package pers.yufiria.craftorithm.recipe;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public class RecipeResult {

    private final @NotNull NamespacedItemId resultItemId;
    private final @NotNull Integer amount;

    public RecipeResult(@NotNull NamespacedItemId resultItemId, @NotNull Integer amount) {
        this.resultItemId = resultItemId;
        this.amount = amount;
    }

    public @NotNull NamespacedItemId resultItemId() {
        return resultItemId;
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
        return resultItemId + " " + amount;
    }

}
