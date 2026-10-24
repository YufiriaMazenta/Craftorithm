package pers.yufiria.craftorithm.recipe.remainder.impl;

import crypticlib.CrypticLib;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.remainder.RemainderProcessor;

import java.util.Optional;

/**
 * 固定返回配置中的物品
 * <p>
 * 持有解析后的{@link NamespacedItemIdStack}, 每次合成时重新向{@link ItemManager}取物品, 不持有物品快照
 */
public record ReplaceRemainderProcessor(NamespacedItemIdStack itemIdStack) implements RemainderProcessor {

    public ReplaceRemainderProcessor(@NotNull NamespacedItemIdStack itemIdStack) {
        this.itemIdStack = copyOf(itemIdStack);
    }

    public static @Nullable ReplaceRemainderProcessor fromConfig(
        @NotNull ConfigurationSection section,
        @NotNull NamespacedKey recipeKey
    ) {
        String itemStr = section.getString("item");
        if (itemStr == null || itemStr.isBlank()) {
            CrypticLib.warn("Missing replacement item in recipe: " + recipeKey);
            return null;
        }

        NamespacedItemIdStack itemIdStack = NamespacedItemIdStack.fromString(itemStr);
        if (itemIdStack == null) {
            CrypticLib.warn("Cannot parse replacement item " + itemStr + " in recipe: " + recipeKey);
            return null;
        }

        Optional<ItemStack> itemOpt = ItemManager.INSTANCE.matchItem(itemIdStack);
        if (itemOpt.isEmpty()) {
            CrypticLib.warn("Replacement item not found: " + itemIdStack + " in recipe: " + recipeKey);
            return null;
        }
        if (itemIdStack.amount() > itemOpt.get().getMaxStackSize()) {
            CrypticLib.warn("Invalid replacement count " + itemIdStack.amount() + " for " + itemIdStack + " in recipe: " + recipeKey);
            return null;
        }
        return new ReplaceRemainderProcessor(itemIdStack);
    }

    @Override
    public @NotNull ItemStack createRemainder(@NotNull ItemStack source) {
        return ItemManager.INSTANCE.matchItem(itemIdStack)
            .map(ItemStack::clone)
            .orElseGet(() -> new ItemStack(Material.AIR));
    }

    @Override
    public @NotNull NamespacedItemIdStack itemIdStack() {
        return copyOf(itemIdStack);
    }

    private static NamespacedItemIdStack copyOf(NamespacedItemIdStack source) {
        return new NamespacedItemIdStack(source.itemId(), source.amount());
    }

}
