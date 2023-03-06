package me.yufiria.craftorithm.listener;

import me.yufiria.craftorithm.CraftorithmAPI;
import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import me.yufiria.craftorithm.recipe.RecipeManager;
import me.yufiria.craftorithm.util.ItemUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum CraftHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOW)
    public void dispatchConditions(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null)
            return;
        YamlConfiguration config = RecipeManager.getRecipeConfig(event.getRecipe());
        if (config == null)
            return;

        Player player = (Player) event.getView().getPlayer();
        String condition = config.getString("condition", "true");
        condition = "if " + condition;
        boolean result = (boolean) ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, condition).getObj();
        if (!result) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void dispatchActions(CraftItemEvent event) {
        if (event.getInventory().getResult() == null) {
            event.getInventory().setResult(null);
            event.setCancelled(true);
            return;
        }
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        YamlConfiguration config = RecipeManager.getRecipeConfig(event.getRecipe());
        if (config == null)
            return;
        Player player = (Player) entity;
        List<String> actions = config.getStringList("actions");
        CraftorithmAPI.INSTANCE.getArcencielDispatcher().dispatchArcencielFunc(player, actions);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void checkCannotCraftLore(PrepareItemCraftEvent event) {
        ItemStack[] items = event.getInventory().getMatrix();
        boolean containsLore = ItemUtil.hasCannotCraftLore(items);
        if (containsLore)
            event.getInventory().setResult(null);
    }

}
