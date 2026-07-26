package pers.yufiria.craftorithm.hook.item;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Bukkit;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.listener.ItemsAdderListener;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE)
})
public enum ItemsAdderHook implements ItemPluginHook {

    INSTANCE;
    private boolean hook = false;

    @Override
    public String pluginName() {
        return "ItemsAdder";
    }

    @Override
    public boolean hook() {
        hook = isPluginEnabled();
        if (hook) {
            BukkitMsgSender.INSTANCE.debug("[Craftorithm] Registering ItemsAdder Listener");
            Bukkit.getPluginManager().registerEvents(ItemsAdderListener.INSTANCE, Craftorithm.instance());
        }
        return hook;
    }

    @Override
    public ItemProvider itemProvider() {
        return ItemsAdderItemProvider.INSTANCE;
    }

    @Override
    public void unhook() {
        if (hook) {
            ItemsAdderLoadDataEvent.getHandlerList().unregister(ItemsAdderListener.INSTANCE);
        }
    }

}
