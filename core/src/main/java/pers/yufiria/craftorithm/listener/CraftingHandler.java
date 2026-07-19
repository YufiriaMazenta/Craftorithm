package pers.yufiria.craftorithm.listener;

import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.EventUtils;

@EventListener
public enum CraftingHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void refreshDynamicResult(PrepareItemCraftEvent event) {
        // 先尝试 Bukkit 原生匹配
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            // Bukkit 匹配到了配方，Craftorithm 配方需要 refresh
            NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
            if (recipeKey != null && recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
                refreshResultItem(event, recipe.getResult());
            }
            return;
        }
    }

    private void refreshResultItem(PrepareItemCraftEvent event, ItemStack item) {
        ItemManager.INSTANCE.matchItemId(item, true)
            .flatMap(id -> EventUtils.getViewer(event)
                .flatMap(player -> ItemManager.INSTANCE.matchItem(id, player))
            )
            .ifPresent(refreshItem -> {
                if (!item.isSimilar(refreshItem)) {
                    item.setItemMeta(refreshItem.getItemMeta());
                    event.getInventory().setResult(item);
                }
            });
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
