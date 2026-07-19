package pers.yufiria.craftorithm.recipe.choice;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class StackableItemIdChoice implements RecipeChoice {

    private final List<NamespacedItemIdStack> itemIds;
    private final Random rand = new Random();

    public StackableItemIdChoice(List<NamespacedItemIdStack> itemIds) {
        if (itemIds == null || itemIds.isEmpty())
            throw new UnsupportedOperationException("ItemIds cannot be null or empty");
        this.itemIds = itemIds;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return ItemManager.INSTANCE.matchItem(itemIds.get(rand.nextInt(itemIds.size()))).orElseThrow();
    }

    @Override
    public @NotNull RecipeChoice clone() {
        return new StackableItemIdChoice(itemIds);
    }

    public int getUseAmount(NamespacedItemId itemId) {
        for (NamespacedItemIdStack stackedItemId : itemIds) {
            if (stackedItemId.itemId().equals(itemId)) {
                return stackedItemId.amount();
            }
        }
        throw new IllegalArgumentException("Do not have this item id: " + itemId);
    }

    @Override
    public boolean test(@NotNull ItemStack itemStack) {
        NamespacedItemIdStack stackedItemId = ItemManager.INSTANCE.matchItemId(itemStack, true)
            .orElseGet(() -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(itemStack.getType()), itemStack.getAmount()));
        NamespacedItemIdStack finalStackedItemId = stackedItemId;
        return itemIds.stream().anyMatch(itemId -> itemId.isSimilar(finalStackedItemId) && finalStackedItemId.amount() >= itemId.amount());
    }

    @Override
    public @NotNull RecipeChoice validate(boolean allowEmptyRecipes) {
        if (this.itemIds.stream().anyMatch((it) -> {
            Optional<ItemStack> itemStack = ItemManager.INSTANCE.matchItem(it);
            return itemStack.isEmpty() || ItemHelper.isAir(itemStack.get());
        })) {
            throw new IllegalArgumentException("RecipeChoice.ExactChoice cannot contain air");
        } else {
            return this;
        }
    }



}
