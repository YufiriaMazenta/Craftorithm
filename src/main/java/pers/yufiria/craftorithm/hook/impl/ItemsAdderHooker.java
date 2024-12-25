package pers.yufiria.craftorithm.hook.impl;

import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.ItemPluginHooker;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.ItemsAdderItemProvider;
import pers.yufiria.craftorithm.listener.hook.ItemsAdderHandler;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.Bukkit;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
)
public enum ItemsAdderHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public String pluginName() {
        return "ItemsAdder";
    }

    @Override
    public boolean hook() {
        boolean hooked = hookByEnabled();
        if (hooked) {
            BukkitMsgSender.INSTANCE.debug("[Craftorithm] Registering ItemsAdder Handler");
            Bukkit.getPluginManager().registerEvents(ItemsAdderHandler.INSTANCE, Craftorithm.instance());
        }
        return hooked;
    }

    @Override
    public ItemProvider itemProvider() {
        return ItemsAdderItemProvider.INSTANCE;
    }

}
