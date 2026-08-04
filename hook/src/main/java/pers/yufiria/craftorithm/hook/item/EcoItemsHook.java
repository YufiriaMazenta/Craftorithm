package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTaskSettings;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskSettings(rules = {
    @LifecycleRule(lifeCycle = Lifecycle.ENABLE)
})
public enum EcoItemsHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "EcoItems";
    }

    @Override
    public ItemProvider itemProvider() {
        return EcoItemsItemProvider.INSTANCE;
    }

}
