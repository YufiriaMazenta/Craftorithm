package pers.yufiria.craftorithm.item;

import org.jetbrains.annotations.NotNull;

public class NamespacedItemIdStack {

    private @NotNull NamespacedItemId itemId;
    private @NotNull Integer amount;

    public NamespacedItemIdStack(@NotNull NamespacedItemId itemId) {
        this.itemId = itemId;
        this.amount = 1;
    }

    public NamespacedItemIdStack(@NotNull NamespacedItemId itemId, @NotNull Integer amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    public @NotNull NamespacedItemId itemId() {
        return itemId;
    }

    public NamespacedItemIdStack setItemId(NamespacedItemId itemId) {
        this.itemId = itemId;
        return this;
    }

    public @NotNull Integer amount() {
        return amount;
    }

    public NamespacedItemIdStack setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof NamespacedItemIdStack that)) return false;

        return itemId.equals(that.itemId) && amount.equals(that.amount);
    }

    public boolean isSimilar(NamespacedItemIdStack that) {
        if (this.equals(that)) return true;
        return this.itemId.equals(that.itemId);
    }

    @Override
    public int hashCode() {
        int result = itemId.hashCode();
        result = 31 * result + amount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return amount <= 1 ? itemId.toString() : itemId + " " + amount;
    }

    public static NamespacedItemIdStack fromString(String string) {
        if (string == null) return null;
        int lastSpaceIndex = string.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            NamespacedItemId itemId = NamespacedItemId.fromString(string);
            if (itemId != null) {
                return new NamespacedItemIdStack(itemId);
            } else {
                return null;
            }
        } else {
            String itemIdStr = string.substring(0, lastSpaceIndex);
            String amountStr = string.substring(lastSpaceIndex + 1).trim();
            try {
                int amount = Integer.parseInt(amountStr);
                NamespacedItemId itemId = NamespacedItemId.fromString(itemIdStr);
                if (itemId == null) {
                    return null;
                }
                return new NamespacedItemIdStack(itemId, amount);
            } catch (NumberFormatException e) {
                int amount = 1;
                NamespacedItemId itemId = NamespacedItemId.fromString(string);
                if (itemId == null) {
                    return null;
                }
                return new NamespacedItemIdStack(itemId, amount);
            }
        }

    }

}