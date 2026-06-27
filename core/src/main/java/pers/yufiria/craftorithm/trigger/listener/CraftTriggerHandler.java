package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import pers.yufiria.craftorithm.trigger.BuiltInTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;

/**
 * 合成触发器监听器
 */
@EventListener
public enum CraftTriggerHandler implements Listener {

    INSTANCE;

    /**
     * Prepare 阶段：检查 deny 条件，清空结果槽拒绝合成
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        TriggerContext ctx = BuiltInTriggerTypes.CRAFTING.extractPrepareContext(event);
        if (ctx == null) return;
        int denied = TriggerManager.INSTANCE.firePrepare(BuiltInTriggerTypes.CRAFTING.typeKey(), ctx);
        if (denied > 0) {
            event.getInventory().setItem(0, null);
        }
    }

    /**
     * 实际阶段：执行 actions
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        TriggerContext ctx = BuiltInTriggerTypes.CRAFTING.extractContext(event);
        if (ctx == null) return;
        TriggerManager.INSTANCE.fire(BuiltInTriggerTypes.CRAFTING.typeKey(), ctx);
    }

}