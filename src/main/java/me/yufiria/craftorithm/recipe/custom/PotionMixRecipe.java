package me.yufiria.craftorithm.recipe.custom;

import me.yufiria.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PotionMixRecipe implements CustomRecipe {

    private NamespacedKey key;
    private ItemStack result;
    private RecipeChoice input;
    private RecipeChoice sourcePotion;

    /**
     * 一个炼药配方
     * @param key 配方的key
     * @param result 配方的结果
     * @param input 配方的酿造材料
     * @param sourcePotion 配方的源药水
     */
    public PotionMixRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull RecipeChoice input, @NotNull RecipeChoice sourcePotion) {
        this.key = key;
        this.result = result;
        this.input = input;
        this.sourcePotion = sourcePotion;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.POTION;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public void setKey(NamespacedKey key) {
        this.key = key;
    }

    public @NotNull ItemStack getResult() {
        return this.result;
    }

    public @NotNull RecipeChoice getInput() {
        return this.input;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public void setInput(RecipeChoice input) {
        this.input = input;
    }

    public void setSourcePotion(RecipeChoice sourcePotion) {
        this.sourcePotion = sourcePotion;
    }

    public @NotNull RecipeChoice getSourcePotion() {
        return this.sourcePotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.result, this.input, this.sourcePotion);
    }

}
