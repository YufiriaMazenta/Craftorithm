package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtil;
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

import java.util.List;

public enum SmithingHandler implements Listener {

    INSTANCE;

    @EventHandler
    public void onPrepareSmith(PrepareSmithingEvent event) {
        NamespacedKey recipeKey = RecipeManager.getRecipeKey(event.getInventory().getRecipe());
        YamlConfiguration config = RecipeManager.getRecipeConfig(recipeKey);
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

    @EventHandler
    public void onSmithItem(SmithItemEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        NamespacedKey recipeKey = RecipeManager.getRecipeKey(event.getInventory().getRecipe());
        YamlConfiguration config = RecipeManager.getRecipeConfig(recipeKey);
        if (config == null)
            return;
        Player player = (Player) entity;
        List<String> actions = config.getStringList("actions");
        CraftorithmAPI.INSTANCE.getArcencielDispatcher().dispatchArcencielFunc(player, actions);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void checkCannotCraftLore(PrepareSmithingEvent event) {
        ItemStack[] items = event.getInventory().getContents();
        boolean containsLore = ItemUtil.hasCannotCraftLore(items);
        if (containsLore)
            event.getInventory().setResult(null);
    }

}
