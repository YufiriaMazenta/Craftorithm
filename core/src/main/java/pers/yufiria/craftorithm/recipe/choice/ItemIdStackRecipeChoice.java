package pers.yufiria.craftorithm.recipe.choice;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.item.ItemGroup;

import java.util.*;

/**
 * 支持物品堆叠的配方材料,目前只有铁砧配方使用
 */
public class ItemIdStackRecipeChoice implements RecipeChoice {

    private final Collection<NamespacedItemId> itemIds;
    private final int amount;
    private final Random rand = new Random();
    /**
     * 该材料引用的物品组ID, 如tag:minecraft:planks或item_pack:my_pack, 非物品组材料为null
     */
    private final @Nullable String itemGroupId;

    public ItemIdStackRecipeChoice(Collection<NamespacedItemId> itemIds, int amount) {
        this(itemIds, amount, null);
    }

    public ItemIdStackRecipeChoice(Collection<NamespacedItemId> itemIds, int amount, @Nullable String itemGroupId) {
        this.amount = amount;
        if (itemIds == null || itemIds.isEmpty())
            throw new UnsupportedOperationException("ItemIds cannot be null or empty");
        this.itemGroupId = itemGroupId;
        if (itemIds.size() >= PluginConfigs.INGREDIENT_USE_SET_THRESHOLD.value()) {
            this.itemIds = Set.copyOf(itemIds);
        } else {
            this.itemIds = List.copyOf(itemIds);
        }
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        //物品组材料使用构建出的物品组占位符作为展示物品
        if (itemGroupId != null) {
            Optional<ItemStack> itemGroupItem = ItemGroup.fromIngredientId(itemGroupId)
                .flatMap(ItemGroup::toPlaceholderItem);
            if (itemGroupItem.isPresent()) {
                ItemStack itemStack = itemGroupItem.get();
                itemStack.setAmount(amount);
                return itemStack;
            }
        }
        int index = rand.nextInt(itemIds.size());
        NamespacedItemIdStack randomItemIdStack = new NamespacedItemIdStack(
            itemIds.stream()
                .skip(index)
                .findFirst()
                .orElseThrow(
                    () -> new ItemNotFoundException("No item at index " + index + " in ItemIdStackRecipeChoice")
                ),
            amount
        );
        return ItemManager.INSTANCE.matchItem(randomItemIdStack)
            .orElseThrow(() -> new ItemNotFoundException("Item not found: " + randomItemIdStack));
    }

    @Override
    public @NotNull RecipeChoice clone() {
        return new ItemIdStackRecipeChoice(itemIds, amount, itemGroupId);
    }

    public int getUseAmount() {
        return amount;
    }

    @Override
    public boolean test(@NotNull ItemStack itemStack) {
        Optional<NamespacedItemIdStack> inputItemIdStackOpt = ItemManager.INSTANCE.matchItemIdOrVanilla(itemStack, false);
        if (inputItemIdStackOpt.isEmpty()) {
            return false;
        }
        NamespacedItemIdStack inputItemIdStack = inputItemIdStackOpt.get();
        if (!itemIds.contains(inputItemIdStack.itemId())) {
            return false;
        }
        return inputItemIdStack.amount() >= amount;
    }

}
