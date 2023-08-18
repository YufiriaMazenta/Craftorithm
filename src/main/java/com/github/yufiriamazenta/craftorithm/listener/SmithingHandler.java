package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.CraftorithmAPI;
import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.recipe.DefRecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtil;
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
//        YamlConfiguration config = DefRecipeManager.getRecipeConfig(event.getInventory().getRecipe());
//        if (config == null)
//            return;
//
//        Player player = (Player) event.getView().getPlayer();
//        String condition = config.getString("condition", "true");
//        condition = "if " + condition;
//        boolean result = (boolean) ArcencielDispatcher.INSTANCE.dispatchArcencielBlock(player, condition).getObj();
//        if (!result) {
//            event.getInventory().setResult(null);
//        }
        //TODO
    }

    @EventHandler
    public void onSmithItem(SmithItemEvent event) {
//        HumanEntity entity = event.getWhoClicked();
//        if (!(entity instanceof Player)) {
//            return;
//        }
//        YamlConfiguration config = DefRecipeManager.getRecipeConfig(event.getInventory().getRecipe());
//        if (config == null)
//            return;
//        Player player = (Player) entity;
//        List<String> actions = config.getStringList("actions");
//        CraftorithmAPI.INSTANCE.getArcencielDispatcher().dispatchArcencielFunc(player, actions);
        //TODO
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void checkCannotCraftLore(PrepareSmithingEvent event) {
        ItemStack[] items = event.getInventory().getContents();
        boolean containsLore = ItemUtil.hasCannotCraftLore(items);
        if (containsLore)
            event.getInventory().setResult(null);
    }

}
