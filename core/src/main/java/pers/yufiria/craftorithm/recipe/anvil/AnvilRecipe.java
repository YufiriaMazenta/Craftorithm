package pers.yufiria.craftorithm.recipe.anvil;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.CustomRecipe;
import pers.yufiria.craftorithm.recipe.choice.ItemIdStackRecipeChoice;

public class AnvilRecipe implements CustomRecipe {

    private NamespacedKey recipeKey;
    private NamespacedItemIdStack result;
    private ItemIdStackRecipeChoice base;
    private ItemIdStackRecipeChoice addition;
    private int costLevel = 0;

    public AnvilRecipe(NamespacedKey recipeKey, NamespacedItemIdStack result, ItemIdStackRecipeChoice base, ItemIdStackRecipeChoice addition) {
        this.recipeKey = recipeKey;
        this.result = result;
        this.base = base;
        this.addition = addition;
    }

    public AnvilRecipe setKey(NamespacedKey recipeKey) {
        this.recipeKey = recipeKey;
        return this;
    }

    public AnvilRecipe setResult(NamespacedItemIdStack result) {
        this.result = result;
        return this;
    }

    public ItemIdStackRecipeChoice base() {
        return base;
    }

    public AnvilRecipe setBase(ItemIdStackRecipeChoice base) {
        this.base = base;
        return this;
    }

    public ItemIdStackRecipeChoice addition() {
        return addition;
    }

    public AnvilRecipe setAddition(ItemIdStackRecipeChoice addition) {
        this.addition = addition;
        return this;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return recipeKey;
    }

    @Override
    public @NotNull ItemStack getResult() {
        return ItemManager.INSTANCE.matchItem(result).orElseThrow(() -> new ItemNotFoundException("Anvil recipe result not found: " + result));
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

}
