package pers.yufiria.craftorithm.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Mutable item substitute that never asks Bukkit for a live ItemFactory/server. */
@SuppressWarnings("deprecation")
public final class RecipeTestItem extends ItemStack {
    private final Material material;
    private int count;

    public RecipeTestItem(Material material) {
        this(material, 1);
    }

    public RecipeTestItem(Material material, int count) {
        this.material = material;
        this.count = count;
    }

    @Override public Material getType() { return material; }
    @Override public int getAmount() { return count; }
    @Override public void setAmount(int amount) { count = amount; }
    @Override public boolean hasItemMeta() { return true; }
    @Override public boolean isSimilar(ItemStack other) {
        return other != null && material == other.getType();
    }
    @Override public RecipeTestItem clone() { return new RecipeTestItem(material, count); }
}
