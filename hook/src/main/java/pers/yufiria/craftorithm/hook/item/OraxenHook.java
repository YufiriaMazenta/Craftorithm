package pers.yufiria.craftorithm.hook.item;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent;
import org.bukkit.Bukkit;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.listener.OraxenListener;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE)
})
public enum OraxenHook implements ItemPluginHook {

    INSTANCE;

    private boolean hook = false;

    @Override
    public String pluginName() {
        return "Oraxen";
    }

    @Override
    public boolean hook() {
        hook = isPluginEnabled();
        if (hook) {
            BukkitMsgSender.INSTANCE.debug("[Craftorithm] Registering Oraxen Listener");
            Bukkit.getPluginManager().registerEvents(OraxenListener.INSTANCE, Craftorithm.instance());
        }
        return hook;
    }

    @Override
    public ItemProvider itemProvider() {
        return OraxenItemProvider.INSTANCE;
    }

    @Override
    public void unhook() {
        if (hook) {
            OraxenItemsLoadedEvent.getHandlerList().unregister(OraxenListener.INSTANCE);
        }
    }

}
