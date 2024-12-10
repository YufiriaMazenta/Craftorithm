package pers.yufiria.craftorithm.listener;

import pers.yufiria.craftorithm.item.ItemManager;
import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

@EventListener
public enum CrafterHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCrafterCraft(CrafterCraftEvent event) {
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
