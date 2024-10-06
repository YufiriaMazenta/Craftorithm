package pers.yufiria.craftorithm.hook.impl;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import pers.yufiria.craftorithm.hook.ItemPluginHooker;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.providers.MythicMobsItemProvider;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ENABLE)
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
