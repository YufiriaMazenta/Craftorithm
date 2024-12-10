package pers.yufiria.craftorithm.listener.hook;

import pers.yufiria.craftorithm.Craftorithm;
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public enum OraxenHandler implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOraxenLoaded(OraxenItemsLoadedEvent event) {
        Craftorithm.instance().reloadPlugin();
//        RecipeManager.INSTANCE.reloadRecipeManager();
//        OtherPluginsListenerProxy.INSTANCE.reloadOtherPluginsListener();
    }

}