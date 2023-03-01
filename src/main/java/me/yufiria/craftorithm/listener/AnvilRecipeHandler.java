package me.yufiria.craftorithm.listener;

import me.yufiria.craftorithm.Craftorithm;
import me.yufiria.craftorithm.recipe.RecipeManager;
import me.yufiria.craftorithm.recipe.custom.AnvilRecipe;
import me.yufiria.craftorithm.util.LangUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum AnvilRecipeHandler implements Listener {

    INSTANCE;

    AnvilRecipeHandler() {}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!Craftorithm.getInstance().getConfig().getBoolean("enable_anvil_recipe", true))
            return;
        ItemStack base = event.getInventory().getItem(0);
        ItemStack addition = event.getInventory().getItem(1);
        AnvilRecipe anvilRecipe = RecipeManager.matchAnvilRecipe(base, addition);
        if (anvilRecipe == null)
            return;
        String renameStr = event.getInventory().getRenameText();
        ItemStack result = anvilRecipe.getResult();
        if (renameStr != null && !renameStr.isEmpty()) {
            ItemMeta meta = result.getItemMeta();
            if (meta != null) {
                renameStr = LangUtil.placeholder((Player) event.getViewers().get(0), renameStr);
                renameStr = LangUtil.color(renameStr);
                meta.setDisplayName(renameStr);
                result.setItemMeta(meta);
            }
        }
        int costLevel = anvilRecipe.getCostLevel();
        event.setResult(result);
        event.getInventory().setRepairCost(costLevel);
    }

}
