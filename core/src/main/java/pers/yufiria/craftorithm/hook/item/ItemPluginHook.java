package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.item.ItemProvider;

public interface ItemPluginHook extends PluginHook, LifeCycleTask {

    ItemProvider itemProvider();

    @Override
    default void lifecycle(Object plugin, LifeCycle lifeCycle) {
        ItemPluginHookManager.INSTANCE.addItemPluginHook(this);
    }

    @Override
    default void unhook() {}

}
