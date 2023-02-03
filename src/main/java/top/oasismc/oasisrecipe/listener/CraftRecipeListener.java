package top.oasismc.oasisrecipe.listener;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.recipe.handler.OldRecipeManager;

import java.util.List;

public enum CraftRecipeListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null)
            return;
        String recipeName = OldRecipeManager.INSTANCE.getRecipeName(event.getRecipe());
        if (recipeName == null)
            return;
        for (HumanEntity human : event.getViewers()) {
            if (!(human instanceof Player)) {
                event.getInventory().setResult(null);
                break;
            }

            Player player = ((Player) human);
            List<String> conditions = OldRecipeManager.INSTANCE.getRecipeFile().getConfig().getStringList(recipeName + ".conditions");
            try {
                if (!OasisRecipe.getInstance().getConditionDispatcher().dispatchConditions(conditions, player)) {
                    event.getInventory().setResult(null);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.getInventory().setResult(null);
                break;
            }

        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getInventory().getResult() == null) {
            event.getInventory().setResult(null);
            event.getWhoClicked().closeInventory();
            return;
        }
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        String recipeName = OldRecipeManager.INSTANCE.getRecipeName(event.getRecipe());
        if (recipeName == null)
            return;
        Player player = (Player) entity;
        List<String> actions = OldRecipeManager.INSTANCE.getRecipeFile().getConfig().getStringList(recipeName + ".actions");
        OasisRecipe.getInstance().getActionDispatcher().dispatchActions(actions, player);
    }

}
