package pers.yufiria.craftorithm.recipe.choice;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.*;

/**
 * 支持物品堆叠的配方材料,目前只有铁砧配方使用
 */
public class ItemIdStackRecipeChoice implements RecipeChoice {

    private final Collection<NamespacedItemIdStack> itemIds;
    private final Map<NamespacedItemId, NamespacedItemIdStack> itemIdsMap;
    private final Random rand = new Random();

    public ItemIdStackRecipeChoice(Collection<NamespacedItemIdStack> itemIds) {
        if (itemIds == null || itemIds.isEmpty())
            throw new UnsupportedOperationException("ItemIds cannot be null or empty");
        if (itemIds.size() >= PluginConfigs.INGREDIENT_USE_SET_THRESHOLD.value()) {
            this.itemIds = Set.copyOf(itemIds);
        } else {
            this.itemIds = List.copyOf(itemIds);
        }
        this.itemIdsMap = new HashMap<>();
        for (NamespacedItemIdStack item : itemIds) {
            itemIdsMap.put(item.itemId(), item);
        }
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        int index = rand.nextInt(itemIds.size());
        NamespacedItemIdStack randomItemIdStack = itemIds.stream().skip(index).findFirst()
            .orElseThrow(() -> new ItemNotFoundException("No item at index " + index + " in ItemIdStackRecipeChoice"));
        return ItemManager.INSTANCE.matchItem(randomItemIdStack)
            .orElseThrow(() -> new ItemNotFoundException("Item not found: " + randomItemIdStack));
    }

    @Override
    public @NotNull RecipeChoice clone() {
        return new ItemIdStackRecipeChoice(itemIds);
    }

    public int getUseAmount(NamespacedItemId itemId) {
        NamespacedItemIdStack stored = itemIdsMap.get(itemId);
        if (stored != null) {
            return stored.amount();
        }
        throw new IllegalArgumentException("Do not have this item id: " + itemId);
    }

    @Override
    public boolean test(@NotNull ItemStack itemStack) {
        NamespacedItemIdStack finalStackedItemId = ItemManager.INSTANCE.matchItemId(itemStack, true)
            .orElseGet(() -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(itemStack.getType()), itemStack.getAmount()));
        NamespacedItemIdStack stored = itemIdsMap.get(finalStackedItemId.itemId());
        return stored != null && finalStackedItemId.amount() >= stored.amount();
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
