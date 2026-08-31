package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum OraxenHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "Oraxen";
    }

    @Override
    public ItemProvider itemProvider() {
        return OraxenItemProvider.INSTANCE;
    }

}
