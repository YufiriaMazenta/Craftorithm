package pers.yufiria.craftorithm.hook.impl;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import pers.yufiria.craftorithm.hook.ItemPluginHooker;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.providers.MMOItemsItemProvider;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ENABLE)
)
public enum MMOItemsHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return MMOItemsItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "MMOItems";
    }

    @Override
    public boolean hook() {
        return false;
    }
}
