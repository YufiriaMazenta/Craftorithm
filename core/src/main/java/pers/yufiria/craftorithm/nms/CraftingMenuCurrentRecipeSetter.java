package pers.yufiria.craftorithm.nms;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;

public interface CraftingMenuCurrentRecipeSetter {

    boolean setCurrentRecipe(CraftingInventory craftingInventory, NamespacedKey recipeKey, Recipe recipe);

}
