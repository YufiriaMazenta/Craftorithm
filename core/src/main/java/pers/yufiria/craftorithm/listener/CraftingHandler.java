package pers.yufiria.craftorithm.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.IOHelper;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import pers.yufiria.craftorithm.api.event.recipe.CraftItemByFingerEvent;
import pers.yufiria.craftorithm.api.event.recipe.PrepareItemCraftByFingerEvent;
import pers.yufiria.craftorithm.util.EventUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.finger.RecipeFingerManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;

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

        // Bukkit 未匹配到任何配方，通过指纹查找
        ItemStack[] matrix = event.getInventory().getMatrix();
        NamespacedKey fingerRecipeKey = RecipeFingerManager.INSTANCE.findRecipeByGrid(matrix);
        if (fingerRecipeKey != null) {
            Recipe fingerRecipe = RecipeManager.INSTANCE.getRecipe(fingerRecipeKey);
            if (fingerRecipe != null) {
                event.getInventory().setResult(fingerRecipe.getResult().clone());
                refreshResultItem(event, event.getInventory().getResult());
                new PrepareItemCraftByFingerEvent(event, fingerRecipeKey).call();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void callFingerCraftEvent(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof CraftingInventory craftingInventory)) {
            return;
        }
        if (craftingInventory.getRecipe() != null) {
            return;
        }
        int slot = event.getSlot();
        if (slot != 0) {
            return;
        }
        ItemStack[] matrix = craftingInventory.getMatrix();
        NamespacedKey fingerRecipeKey = RecipeFingerManager.INSTANCE.findRecipeByGrid(matrix);
        if (fingerRecipeKey != null) {
            new CraftItemByFingerEvent(craftingInventory, fingerRecipeKey, event).call();
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        IOHelper.info("1");
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
