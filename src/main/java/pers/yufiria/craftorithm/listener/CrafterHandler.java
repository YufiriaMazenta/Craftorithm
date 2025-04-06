package pers.yufiria.craftorithm.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.block.BlockState;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

@EventListener
public enum CrafterHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkCanCraft(CrafterCraftEvent event) {
        BlockState blockState = event.getBlock().getState();
        if (!(blockState instanceof Crafter crafter))
            return;
        if (ItemManager.INSTANCE.containsCannotCraftItem(crafter.getInventory().getContents())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshResult(CrafterCraftEvent event) {
        ItemStack result = event.getResult();
        if (ItemHelper.isAir(result))
            return;
        NamespacedItemIdStack id = ItemManager.INSTANCE.matchItemId(result, true);
        if (id == null) {
            return;
        }
        ItemStack refreshItem = ItemManager.INSTANCE.matchItem(id);
        if (result.isSimilar(refreshItem)) {
            return;
        }
        result.setItemMeta(refreshItem.getItemMeta());
        event.setResult(result);
    }

}
