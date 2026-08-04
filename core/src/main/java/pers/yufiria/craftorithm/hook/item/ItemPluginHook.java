package pers.yufiria.craftorithm.hook.item;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleTask;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.item.ItemProvider;

public interface ItemPluginHook extends PluginHook, LifecycleTask {

    ItemProvider itemProvider();

    @Override
    default void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        ItemPluginHookManager.INSTANCE.addItemPluginHook(this);
    }

    @Override
    default void unhook() {}

}
