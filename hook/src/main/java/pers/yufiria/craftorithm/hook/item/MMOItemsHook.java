package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTaskSettings;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskSettings(rules = {
    @LifecycleRule(lifeCycle = Lifecycle.ENABLE)
})
public enum MMOItemsHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return MMOItemsItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "MMOItems";
    }

}
