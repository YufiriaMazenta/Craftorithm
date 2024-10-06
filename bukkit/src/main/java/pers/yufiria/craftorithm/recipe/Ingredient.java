package pers.yufiria.craftorithm.recipe;

import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.NamespacedItemId;

public class Ingredient {

    private final @NotNull NamespacedItemId ingredientId;
    private final @NotNull Integer amount;

    public Ingredient(@NotNull NamespacedItemId ingredientId, @NotNull Integer amount) {
        this.ingredientId = ingredientId;
        this.amount = amount;
    }

    public @NotNull NamespacedItemId ingredientId() {
        return ingredientId;
    }

    public @NotNull Integer amount() {
        return amount;
    }



}
