package top.oasismc.oasisrecipe.listener;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.Recipe;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.recipe.RecipeManager;

import java.util.List;

public enum SmithingListener implements Listener {

    INSTANCE;

    @EventHandler
    public void onPrepareSmith(PrepareSmithingEvent event) {
        Recipe recipe = event.getInventory().getRecipe();
        String recipeName = RecipeManager.INSTANCE.getRecipeName(recipe);
        if (recipeName == null)
            return;
        for (HumanEntity human : event.getViewers()) {
            if (!(human instanceof Player)) {
                event.setResult(null);
                break;
            }

            Player player = ((Player) human);
            List<String> conditions = RecipeManager.INSTANCE.getRecipeFile().getConfig().getStringList(recipeName + ".conditions");
            try {
                if (!OasisRecipe.getInstance().getConditionDispatcher().dispatchConditions(conditions, player)) {
                    event.setResult(null);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.setResult(null);
                break;
            }

        }
    }

    @EventHandler
    public void onSmithItem(SmithItemEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        Recipe recipe = event.getInventory().getRecipe();
        String recipeName = RecipeManager.INSTANCE.getRecipeName(recipe);
        if (recipeName == null)
            return;
        Player player = (Player) entity;
        List<String> actions = RecipeManager.INSTANCE.getRecipeFile().getConfig().getStringList(recipeName + ".actions");
        OasisRecipe.getInstance().getActionDispatcher().dispatchActions(actions, player);
    }

}
