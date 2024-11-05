package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.listener.BukkitListener;
import crypticlib.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;

@BukkitListener
public class CrafterHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCrafterCraft(CrafterCraftEvent event) {
        ItemStack result = event.getResult();
        if (ItemUtil.isAir(result))
            return;
        String id = ItemManager.INSTANCE.matchItemName(result, false);
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
