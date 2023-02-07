package top.oasismc.oasisrecipe.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.recipe.RecipeManager;

import java.util.List;

public enum CraftRecipeListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null)
            return;
        YamlConfiguration config = RecipeManager.getRecipeConfig(event.getRecipe());
        if (config == null)
            return;
        for (HumanEntity human : event.getViewers()) {
            if (!(human instanceof Player)) {
                event.getInventory().setResult(null);
                break;
            }
            Player player = ((Player) human);

            List<String> conditions = config.getStringList("conditions");
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
        YamlConfiguration config = RecipeManager.getRecipeConfig(event.getRecipe());
        if (config == null)
            return;
        Player player = (Player) entity;
        List<String> actions = config.getStringList("actions");
        OasisRecipe.getInstance().getActionDispatcher().dispatchActions(actions, player);
    }

}
