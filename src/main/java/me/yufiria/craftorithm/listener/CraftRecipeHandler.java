package me.yufiria.craftorithm.listener;

import me.yufiria.craftorithm.recipe.RecipeManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.yufiria.craftorithm.Craftorithm;
import me.yufiria.craftorithm.util.LangUtil;

import java.util.List;

public enum CraftRecipeHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOW)
    public void dispatchConditions(PrepareItemCraftEvent event) {
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
//            try {
//                if (!Craftorithm.getInstance().getConditionDispatcher().dispatchConditions(conditions, player)) {
//                    event.getInventory().setResult(null);
//                    break;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                event.getInventory().setResult(null);
//                break;
//            }
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
//        Craftorithm.getInstance().getActionDispatcher().dispatchActions(actions, player);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void checkCannotCraftLore(PrepareItemCraftEvent event) {
        ItemStack[] items = event.getInventory().getMatrix();
        boolean containsLore = false;
        for (ItemStack item : items) {
            if (item == null)
                continue;
            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                return;
            List<String> lore = item.getItemMeta().getLore();
            if (lore == null)
                return;
            String cannotCraftLoreStr = LangUtil.color(Craftorithm.getInstance().getConfig().getString("lore_cannot_craft", "lore_cannot_craft"));
            for (String loreStr : lore) {
                if (loreStr.equals(cannotCraftLoreStr)) {
                    containsLore = true;
                    break;
                }
            }
            if (containsLore)
                break;
        }
        if (!containsLore)
            return;
        event.getInventory().setResult(null);
    }

}
