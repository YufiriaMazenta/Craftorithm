package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.listener.EventListener;
import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.script.ScriptValue;
import pers.yufiria.craftorithm.trigger.CraftTriggerTypes;
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
        TriggerContext ctx = CraftTriggerTypes.CRAFTING.extractPrepareContext(event);
        if (ctx == null) return;
        int denied = TriggerManager.INSTANCE.firePrepare(CraftTriggerTypes.CRAFTING.typeKey(), ctx);
        if (denied > 0) {
            event.getInventory().setItem(0, null);
        }
    }

    /**
     * 实际阶段：执行 actions
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        TriggerContext ctx = CraftTriggerTypes.CRAFTING.extractContext(event);
        if (ctx == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getInventory().getResult();
        ItemStack[] matrix = event.getInventory().getMatrix();
        ctx.setVariable("craft_num", ScriptValue.of(calculateCraftNum(event.getClick(), matrix, result, player)));
        TriggerManager.INSTANCE.fire(CraftTriggerTypes.CRAFTING.typeKey(), ctx);
    }

    private int calculateCraftNum(ClickType click, ItemStack[] matrix, ItemStack result, Player player) {
        // 普通点击只合成1个
        if (click != ClickType.SHIFT_LEFT
            && click != ClickType.SHIFT_RIGHT
            && click != ClickType.CONTROL_DROP) {
            return 1;
        }
        if (matrix == null) return 0;
        int minIngredientAmount = Integer.MAX_VALUE;
        for (ItemStack item : matrix) {
            if (item == null || item.isEmpty()) continue;
            minIngredientAmount = Math.min(minIngredientAmount, item.getAmount());
        }
        if (minIngredientAmount == Integer.MAX_VALUE) return 1;
        // Ctrl+丢弃：合成最大数量，不受背包空间限制
        if (click == ClickType.CONTROL_DROP) {
            return minIngredientAmount;
        }
        if (ItemHelper.isAir(result)) return 1;
        int resultAmount = result.getAmount();
        // 计算背包能装下多少个结果物品（向上取整，适配原版行为）
        int canFit = calculateCanFit(player, result);
        int canFitTimes = (canFit + resultAmount - 1) / resultAmount;
        return Math.max(1, Math.min(minIngredientAmount, canFitTimes));
    }

    private int calculateCanFit(Player player, ItemStack result) {
        if (ItemHelper.isAir(result)) return 0;
        int maxStack = result.getType().getMaxStackSize();
        int space = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.isEmpty()) {
                space += maxStack;
            } else if (item.isSimilar(result)) {
                space += maxStack - item.getAmount();
            }
        }
        return space;
    }

}