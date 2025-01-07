package com.github.yufiriamazenta.craftorithm.hook.impl;

import com.github.yufiriamazenta.craftorithm.hook.ItemPluginHooker;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.NexoItemProvider;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;

@AutoTask(rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE))
public enum NexoHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return NexoItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "Nexo";
    }

    @Override
    public boolean hook() {
        return hookByEnabled();
    }

}
