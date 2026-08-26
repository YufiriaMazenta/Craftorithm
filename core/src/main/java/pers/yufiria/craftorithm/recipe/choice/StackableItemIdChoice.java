package pers.yufiria.craftorithm.recipe.choice;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * 支持物品堆叠的配方材料,目前只有铁砧配方使用
 */
public class StackableItemIdChoice implements RecipeChoice {

    private final Set<NamespacedItemIdStack> itemIds;
    private final Random rand = new Random();

    public StackableItemIdChoice(Set<NamespacedItemIdStack> itemIds) {
        if (itemIds == null || itemIds.isEmpty())
            throw new UnsupportedOperationException("ItemIds cannot be null or empty");
        this.itemIds = itemIds;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        int index = rand.nextInt(itemIds.size());
        NamespacedItemIdStack randomItemIdStack = itemIds.stream().skip(index).findFirst().orElseThrow();
        return ItemManager.INSTANCE.matchItem(randomItemIdStack).orElseThrow();
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
        NamespacedItemIdStack finalStackedItemId = ItemManager.INSTANCE.matchItemId(itemStack, true)
            .orElseGet(() -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(itemStack.getType()), itemStack.getAmount()));
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
