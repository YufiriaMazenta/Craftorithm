package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import pers.yufiria.craftorithm.trigger.BuiltInTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;

/**
 * 铁砧触发器监听器
 */
@EventListener
public enum AnvilTriggerListener implements Listener {

    INSTANCE;

    /**
     * Prepare 阶段：检查 deny 条件，清空结果槽拒绝铁砧操作
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        TriggerContext ctx = BuiltInTriggerTypes.ANVIL.extractPrepareContext(event);
        if (ctx == null) return;
        int denied = TriggerManager.INSTANCE.firePrepare(BuiltInTriggerTypes.ANVIL.typeKey(), ctx);
        if (denied > 0) {
            event.setResult(null);
            event.getInventory().setItem(2, null);
            event.getInventory().setRepairCost(0);
        }
    }

    /**
     * 实际阶段：执行 actions
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAnvilClick(InventoryClickEvent event) {
        TriggerContext ctx = BuiltInTriggerTypes.ANVIL.extractContext(event);
        if (ctx == null) return;
        TriggerManager.INSTANCE.fire(BuiltInTriggerTypes.ANVIL.typeKey(), ctx);
    }

}
