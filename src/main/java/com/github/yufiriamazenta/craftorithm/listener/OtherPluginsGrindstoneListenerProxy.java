package com.github.yufiriamazenta.craftorithm.listener;

import crypticlib.CrypticLib;
import crypticlib.listener.BukkitListener;
import crypticlib.platform.IPlatform;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.plugin.RegisteredListener;

import java.util.List;

/**
 * 因为Grindstone在1.19才有,所以要独立出来
 */
@BukkitListener
public enum OtherPluginsGrindstoneListenerProxy implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.HIGHEST);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.MONITOR);
    }

    public void proxyPrepareGrindstone(PrepareGrindstoneEvent event, EventPriority priority) {
        //因为只有paper及下游服务端才有这个问题,如果识别到是bukkit或者spigot,就不用处理
        if (CrypticLib.platform().platform().equals(IPlatform.Platform.BUKKIT)) {
            return;
        }
        OtherPluginsListenerProxy proxy = OtherPluginsListenerProxy.INSTANCE;
        List<RegisteredListener> registeredListeners = proxy.getPrepareItemCraftEventListeners().get(priority);
        if (registeredListeners == null || registeredListeners.isEmpty()) {
            return;
        }
        proxy.executeListener(event, registeredListeners);
    }

}
