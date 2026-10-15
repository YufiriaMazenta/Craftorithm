package pers.yufiria.craftorithm.trigger.listener;

import crypticlib.util.ItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 配方触发器监听器的公共判定逻辑
 */
public final class TriggerListenerUtils {

    private TriggerListenerUtils() {}

    /**
     * 判定本次点击是否真的产出了物品, 只有产出的点击才应该执行触发器 actions。
     * 鼠标上带着物品时, 只有 shift 点击才会把产物取出, 其余点击类型只是预览。
     * @param event 合成/锻造产物的点击事件
     * @return 是否真的产出了物品
     */
    public static boolean isRealCraftClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return false;
        }
        ItemStack cursorItem = event.getCursor();
        if (ItemHelper.isAir(cursorItem)) {
            return true;
        }
        //如果鼠标上有物品,只有shift click类型的点击类型真的能触发合成
        return event.getClick().isShiftClick();
    }

}
