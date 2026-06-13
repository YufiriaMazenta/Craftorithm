package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE)
})
public enum OraxenHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "Oraxen";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }

    @Override
    public ItemProvider itemProvider() {
        return OraxenItemProvider.INSTANCE;
    }

}
