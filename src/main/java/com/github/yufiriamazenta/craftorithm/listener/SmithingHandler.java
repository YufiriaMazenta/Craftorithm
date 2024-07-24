package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

@EventListener
public enum SmithingHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void refreshResult(PrepareSmithingEvent event) {
        if (event.getResult() == null)
            return;
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null)
            return;
        NamespacedKey namespacedKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (!namespacedKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            return;
        }

        ItemStack item = event.getResult();
        String id = ItemManager.INSTANCE.matchItemName(item, false);
        if (id == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id, (Player) event.getViewers().get(0));
        if (item.isSimilar(refreshItem)) {
            return;
        }
        item.setItemMeta(refreshItem.getItemMeta());
        event.setResult(item);
        event.getInventory().setResult(item);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void runConditions(PrepareSmithingEvent event) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(event.getInventory().getRecipe());
        if (recipeKey == null)
            return;
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(recipeKey);
        if (config == null)
            return;

        Player player = (Player) event.getView().getPlayer();
        String condition = config.getString("condition", "true");
        condition = "if " + condition;
        boolean result = (boolean) ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, condition).obj();
        if (!result) {
            event.setResult(null);
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void runActions(SmithItemEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(event.getInventory().getRecipe());
        if (recipeKey == null)
            return;
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(recipeKey);
        if (config == null)
            return;
        Player player = (Player) entity;
        List<String> actions = config.getStringList("actions");
        CraftorithmAPI.INSTANCE.arcencielDispatcher().dispatchArcencielFunc(player, actions);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkCannotCraftLore(PrepareSmithingEvent event) {
        ItemStack[] items = event.getInventory().getContents();
        boolean containsLore = ItemUtils.hasCannotCraftLore(items);
        if (containsLore) {
            event.getInventory().setResult(null);
            event.setResult(null);
        }
    }

}
