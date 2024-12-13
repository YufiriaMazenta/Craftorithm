package pers.yufiria.craftorithm.recipe.extra;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.CustomRecipe;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;

public class AnvilRecipe implements CustomRecipe {

    private NamespacedKey key;
    private NamespacedItemIdStack result;
    private StackableItemIdChoice base;
    private StackableItemIdChoice addition;
    private int costLevel = 0;
    private boolean copyNbt = false;
    private boolean copyEnchantments = true;

    public AnvilRecipe(NamespacedKey key, NamespacedItemIdStack result, StackableItemIdChoice base, StackableItemIdChoice addition) {
        this.key = key;
        this.result = result;
        this.base = base;
        this.addition = addition;
    }

    public AnvilRecipe setKey(NamespacedKey key) {
        this.key = key;
        return this;
    }

    public AnvilRecipe setResult(NamespacedItemIdStack result) {
        this.result = result;
        return this;
    }

    public StackableItemIdChoice base() {
        return base;
    }

    public AnvilRecipe setBase(StackableItemIdChoice base) {
        this.base = base;
        return this;
    }

    public StackableItemIdChoice addition() {
        return addition;
    }

    public AnvilRecipe setAddition(StackableItemIdChoice addition) {
        this.addition = addition;
        return this;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull ItemStack getResult() {
        return ItemManager.INSTANCE.matchItem(result);
    }

    public NamespacedItemIdStack result() {
        return result;
    }

    public int costLevel() {
        return costLevel;
    }

    public AnvilRecipe setCostLevel(int costLevel) {
        this.costLevel = costLevel;
        return this;
    }

    public boolean copyNbt() {
        return copyNbt;
    }

    public AnvilRecipe setCopyNbt(boolean copyNbt) {
        this.copyNbt = copyNbt;
        return this;
    }

    public boolean copyEnchantments() {
        return copyEnchantments;
    }

    public AnvilRecipe setCopyEnchantments(boolean copyEnchantments) {
        this.copyEnchantments = copyEnchantments;
        return this;
    }

}
