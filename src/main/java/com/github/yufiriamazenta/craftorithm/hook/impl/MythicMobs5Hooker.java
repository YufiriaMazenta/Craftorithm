package com.github.yufiriamazenta.craftorithm.hook.impl;

import com.github.yufiriamazenta.craftorithm.hook.ItemPluginHooker;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.MythicMobsItemProvider;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
)
public enum MythicMobs5Hooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public String pluginName() {
        return "MythicMobs";
    }

    @Override
    public boolean hook() {
        return hookByEnabled();
    }

    @Override
    public ItemProvider itemProvider() {
        return MythicMobsItemProvider.INSTANCE;
    }

}
