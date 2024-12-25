package com.github.yufiriamazenta.craftorithm.hook.impl;

import com.github.yufiriamazenta.craftorithm.hook.ItemPluginHooker;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.ExecutableItemsItemProvider;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
)
public enum ExecutableItemsHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public String pluginName() {
        return "ExecutableItems";
    }

    @Override
    public boolean hook() {
        return hookByEnabled();
    }

    @Override
    public ItemProvider itemProvider() {
        return ExecutableItemsItemProvider.INSTANCE;
    }
}
