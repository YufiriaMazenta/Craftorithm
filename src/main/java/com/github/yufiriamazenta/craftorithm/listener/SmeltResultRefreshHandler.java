package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.listener.BukkitListener;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

@BukkitListener
public class SmeltResultRefreshHandler implements Listener {

    @EventHandler
    public void refreshSmeltResult(FurnaceSmeltEvent event) {
        Recipe recipe = event.getRecipe();
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }
        ItemStack result = event.getResult();
        String id = ItemManager.INSTANCE.matchItemName(result, false);
        if (id == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id);
        result.setItemMeta(refreshItem.getItemMeta());
        event.setResult(result);
    }

    @EventHandler
    public void refreshBlockCookResult(BlockCookEvent event) {
        Recipe recipe = event.getRecipe();
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }
        ItemStack result = event.getResult();
        String id = ItemManager.INSTANCE.matchItemName(result, false);
        if (id == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id);
        result.setItemMeta(refreshItem.getItemMeta());
        event.setResult(result);
    }

}
