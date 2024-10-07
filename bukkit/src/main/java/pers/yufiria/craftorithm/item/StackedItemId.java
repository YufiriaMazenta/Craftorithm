package pers.yufiria.craftorithm.item;

import org.jetbrains.annotations.NotNull;

public class StackedItemId {

    private @NotNull NamespacedItemId itemId;
    private @NotNull Integer amount;

    public StackedItemId(@NotNull NamespacedItemId itemId, @NotNull Integer amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    public @NotNull NamespacedItemId itemId() {
        return itemId;
    }

    public StackedItemId setItemId(NamespacedItemId itemId) {
        this.itemId = itemId;
        return this;
    }

    public @NotNull Integer amount() {
        return amount;
    }

    public StackedItemId setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

}
