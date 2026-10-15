package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.trigger.CraftTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;

/**
 * 锻造台触发器监听器
 */
@EventListener
public enum SmithingTriggerHandler implements Listener {

    INSTANCE;

    /**
     * Prepare 阶段：检查 deny 条件，清空结果槽拒绝锻造
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        if (!TriggerManager.INSTANCE.hasTrigger(
            CraftTriggerTypes.SMITHING,
            RecipeManager.INSTANCE.getRecipeKey(event.getInventory().getRecipe())
        )) {
            //如果没有配方对应的触发器，直接返回
            return;
        }
        TriggerContext ctx = CraftTriggerTypes.SMITHING.extractPrepareContext(event);
        if (ctx == null) return;
        int denied = TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.SMITHING, ctx);
        if (denied > 0) {
            event.setResult(null);
            event.getInventory().setResult(null);
        }
    }

    /**
     * 实际阶段：执行 actions
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSmithing(SmithItemEvent event) {
        if (!TriggerManager.INSTANCE.hasTrigger(
            CraftTriggerTypes.SMITHING,
            RecipeManager.INSTANCE.getRecipeKey(event.getInventory().getRecipe())
        )) {
            //如果没有配方对应的触发器，直接返回
            return;
        }
        TriggerContext ctx = CraftTriggerTypes.SMITHING.extractContext(event);
        if (ctx == null) return;
        if (!TriggerListenerUtils.isRealCraftClick(event)) return;
        TriggerManager.INSTANCE.fire(CraftTriggerTypes.SMITHING, ctx);
    }

}
