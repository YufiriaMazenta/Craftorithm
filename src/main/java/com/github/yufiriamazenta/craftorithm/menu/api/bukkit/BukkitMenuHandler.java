package com.github.yufiriamazenta.craftorithm.menu.api.bukkit;

import com.github.yufiriamazenta.craftorithm.menu.api.IMenuHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bukkit的菜单处理器
 */
public abstract class BukkitMenuHandler implements IMenuHandler<Integer, InventoryClickEvent, ItemDisplayIcon>, InventoryHolder {

    private final Map<Integer, ItemDisplayIcon> menuIconMap;

    protected BukkitMenuHandler(Map<Integer, ItemDisplayIcon> menuIconMap) {
        this.menuIconMap = menuIconMap;
    }

    protected BukkitMenuHandler() {
        this.menuIconMap = new ConcurrentHashMap<>();
    }

    @Override
    public Map<Integer, ItemDisplayIcon> menuIconMap() {
        return menuIconMap;
    }

    @Override
    public void click(Integer slot, InventoryClickEvent clickEvent) {
        if (!menuIconMap.containsKey(slot))
            return;
        menuIconMap.get(slot).onClick(clickEvent);
    }

}
