package pers.yufiria.craftorithm.hook.item;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleTask;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.item.ItemProvider;

public interface ItemPluginHook extends PluginHook, LifecycleTask {

    ItemProvider itemProvider();

    @Override
    default void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase phase) {
        ItemPluginHookManager.INSTANCE.addItemPluginHook(this);
    }

}
