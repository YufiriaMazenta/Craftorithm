package pers.yufiria.craftorithm.item.test;

import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import pers.yufiria.craftorithm.hook.item.ItemPluginHook;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifeCycleTaskSettings(
    rules = @TaskRule(lifeCycle = LifeCycle.ENABLE)
)
public enum TestHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return TestItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "Test";
    }

    @Override
    public boolean hook() {
        return true;
    }

}
