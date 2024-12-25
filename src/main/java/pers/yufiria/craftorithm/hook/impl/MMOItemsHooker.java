package pers.yufiria.craftorithm.hook.impl;

import pers.yufiria.craftorithm.hook.ItemPluginHooker;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.MMOItemsItemProvider;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
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
        return hookByEnabled();
    }
}
