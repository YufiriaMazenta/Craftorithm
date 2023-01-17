package top.oasismc.oasisrecipe.recipe.object.advanced;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class AnvilRecipe implements Recipe {

    private ItemStack result;
    private ItemStack rawMaterial;
    private int brewingTick;
    private ItemStack sourcePotion;
    private NamespacedKey key;

    public AnvilRecipe(NamespacedKey key, ItemStack result) {
        this(key, result, null, 400, null);
    }

    public AnvilRecipe(NamespacedKey key, ItemStack result, ItemStack rawMaterial, int brewingTick, ItemStack sourcePotion) {
        this.result = result;
        this.rawMaterial = rawMaterial;
        this.brewingTick = brewingTick;
        this.sourcePotion = sourcePotion;
    }

    @Override
    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public ItemStack getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(ItemStack rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public int getBrewingTick() {
        return brewingTick;
    }

    public void setBrewingTick(int brewingTick) {
        this.brewingTick = brewingTick;
    }

    public ItemStack getSourcePotion() {
        return sourcePotion;
    }

    public void setSourcePotion(ItemStack sourcePotion) {
        this.sourcePotion = sourcePotion;
    }

}
