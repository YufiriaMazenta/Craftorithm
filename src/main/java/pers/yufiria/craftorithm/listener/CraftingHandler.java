package pers.yufiria.craftorithm.listener;

import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

@EventListener
public enum CraftingHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void refreshResult(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null)
            return;
        Recipe recipe = event.getRecipe();
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (namespacedKey == null) {
            throw new RuntimeException("Can not get recipe key");
        }
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }
        ItemStack item = event.getRecipe().getResult();
        NamespacedItemIdStack itemId = ItemManager.INSTANCE.matchItemId(item, true);
        if (itemId == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(itemId, (Player) event.getViewers().get(0));
        if (item.isSimilar(refreshItem)) {
            return;
        }
        item.setItemMeta(refreshItem.getItemMeta());
        event.getInventory().setResult(item);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkCannotCraft(PrepareItemCraftEvent event) {
        ItemStack[] items = event.getInventory().getMatrix();
        boolean cannotCraft = ItemManager.INSTANCE.containsCannotCraftItem(items);
        if (cannotCraft) {
            event.getInventory().setResult(null);
        }
    }

}
