package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.manager.DefItemManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public enum CraftorithmAPI {

    INSTANCE;

    public ItemStack getOasisRecipeItem(String itemName) {
        return DefItemManager.getCraftorithmItem(itemName);
    }

    public ArcencielDispatcher getArcencielDispatcher() {
        return ArcencielDispatcher.INSTANCE;
    }

    public List<Recipe> getRecipes() {
        //TODO
        return null;
    }

}
