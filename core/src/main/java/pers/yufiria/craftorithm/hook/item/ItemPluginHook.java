package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.item.ItemProvider;

public interface ItemPluginHook extends PluginHook, BukkitLifeCycleTask {

    ItemProvider itemProvider();

    @Override
    default void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        ItemPluginHookManager.INSTANCE.addItemPluginHook(this);
    }

    @Override
    default void unhook() {}

}
