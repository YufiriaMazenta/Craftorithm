package me.yufiria.craftorithm.recipe.custom;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class AnvilRecipeItem {

    private ItemStack item;
    private Boolean checkMeta;
    private int cost;

    public AnvilRecipeItem(ItemStack item, Boolean checkMeta) {
        this.item = item;
        this.checkMeta = checkMeta;
        this.cost = item.getAmount();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof AnvilRecipeItem))
            return false;
        if (obj == this)
            return true;
        ItemStack otherItem = ((AnvilRecipeItem) obj).getItem();
        if (checkMeta) {
            return this.item.isSimilar(otherItem);
        }
        return this.item.getType().equals(otherItem.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, checkMeta);
    }

    public boolean check(ItemStack otherItem) {
        if (checkMeta) {
            return this.item.isSimilar(otherItem) && otherItem.getAmount() >= this.item.getAmount();
        }
        return this.item.getType().equals(otherItem.getType());
    }

    public AnvilRecipeItem build(ItemStack item, Boolean checkMeta) {
        return new AnvilRecipeItem(item, checkMeta);
    }

    public Boolean getCheckMeta() {
        return checkMeta;
    }

    public void setCheckMeta(Boolean checkMeta) {
        this.checkMeta = checkMeta;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
