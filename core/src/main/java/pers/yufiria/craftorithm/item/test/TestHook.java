package pers.yufiria.craftorithm.item.test;

import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTaskSettings;
import pers.yufiria.craftorithm.hook.item.ItemPluginHook;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskSettings(
    rules = @LifecycleRule(lifeCycle = Lifecycle.ENABLE)
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
