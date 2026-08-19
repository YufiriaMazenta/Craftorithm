package pers.yufiria.craftorithm.listener;

import crypticlib.CrypticLib;
import crypticlib.listener.EventListener;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pers.yufiria.craftorithm.Craftorithm;

/**
 * 用于处理数据包重载后导致插件配方失效的问题
 * 只在paper端和衍生端有效
 */
@EventListener
public enum ServerResourcesReloadListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerResourceReload(ServerResourcesReloadedEvent event) {
        CrypticLib.info("Plugin reloaded automatically upon server resources reload");
        Craftorithm.instance().reloadPlugin();
    }

}
