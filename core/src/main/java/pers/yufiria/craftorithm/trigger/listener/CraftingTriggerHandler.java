package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.trigger.CraftTriggerTypes;
import pers.yufiria.craftorithm.trigger.TriggerContext;
import pers.yufiria.craftorithm.trigger.TriggerManager;

/**
 * 合成触发器监听器
 */
@EventListener
public enum CraftingTriggerHandler implements Listener {

    INSTANCE;

    /**
     * Prepare 阶段：检查 deny 条件，清空结果槽拒绝合成
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (!TriggerManager.INSTANCE.hasTrigger(
            CraftTriggerTypes.CRAFTING,
            RecipeManager.INSTANCE.getRecipeKey(event.getRecipe())
        )) {
            //如果没有配方对应的触发器，直接返回
            return;
        }
        TriggerContext ctx = CraftTriggerTypes.CRAFTING.extractPrepareContext(event);
        if (ctx == null) return;
        int denied = TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.CRAFTING, ctx);
        if (denied > 0) {
            event.getInventory().setItem(0, null);
        }
    }

    /**
     * 实际阶段：执行 actions
     * 放在 HIGHEST 并忽略已取消事件: 这样其它插件(区域保护/反作弊)取消合成时我们不会提前发奖
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!TriggerManager.INSTANCE.hasTrigger(
            CraftTriggerTypes.CRAFTING,
            RecipeManager.INSTANCE.getRecipeKey(event.getRecipe())
        )) {
            //如果没有配方对应的触发器，直接返回
            return;
        }

        TriggerContext ctx = CraftTriggerTypes.CRAFTING.extractContext(event);
        if (ctx == null) return;
        if (!TriggerListenerUtils.isRealCraftClick(event)) return;
        TriggerManager.INSTANCE.fire(CraftTriggerTypes.CRAFTING, ctx);
    }

}