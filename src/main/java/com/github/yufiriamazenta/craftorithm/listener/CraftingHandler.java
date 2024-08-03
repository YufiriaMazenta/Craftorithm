package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.listener.BukkitListener;
import crypticlib.util.InventoryUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

@BukkitListener
public enum CraftingHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void refreshResult(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null)
            return;
        Recipe recipe = event.getRecipe();
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }
        ItemStack item = event.getRecipe().getResult();
        String id = ItemManager.INSTANCE.matchItemName(item, false);
        if (id == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id, (Player) event.getViewers().get(0));
        if (item.isSimilar(refreshItem)) {
            return;
        }
        item.setItemMeta(refreshItem.getItemMeta());
        event.getInventory().setResult(item);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void runConditions(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null)
            return;
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(RecipeManager.INSTANCE.getRecipeKey(event.getRecipe()));
        if (config == null)
            return;

        Object inventoryView = InventoryUtil.getInventoryView(event);
        Player player = (Player) InventoryUtil.getInventoryViewPlayer(inventoryView);
        String condition = config.getString("condition", "true");
        condition = "if " + condition;
        boolean result = (boolean) ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, condition).obj();
        if (!result) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void runActions(CraftItemEvent event) {
        if (event.getInventory().getResult() == null) {
            event.getInventory().setResult(null);
            event.setCancelled(true);
            return;
        }
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(event.getRecipe());
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(recipeKey);
        if (config == null)
            return;
        Player player = (Player) entity;
        List<String> actions = config.getStringList("actions");
        CraftorithmAPI.INSTANCE.arcencielDispatcher().dispatchArcencielFunc(player, actions);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkCannotCraft(PrepareItemCraftEvent event) {
        ItemStack[] items = event.getInventory().getMatrix();
        boolean containsLore = ItemUtils.hasCannotCraftLore(items);
        if (containsLore) {
            event.getInventory().setResult(null);
        }
    }

}
