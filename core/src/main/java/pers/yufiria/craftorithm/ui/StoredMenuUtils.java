package pers.yufiria.craftorithm.ui;

import crypticlib.ui.menu.StoredMenu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

/**
 * {@link StoredMenu} 相关的公共工具
 */
public final class StoredMenuUtils {

    private StoredMenuUtils() {}

    /**
     * 取出点击事件所属菜单当前存储的物品
     * @param event 菜单内的点击事件
     * @return 槽位 -> 物品的映射
     */
    public static Map<Integer, ItemStack> storedItems(InventoryClickEvent event) {
        StoredMenu menu = (StoredMenu) Objects.requireNonNull(event.getClickedInventory()).getHolder();
        return Objects.requireNonNull(menu).storedItems();
    }

}
