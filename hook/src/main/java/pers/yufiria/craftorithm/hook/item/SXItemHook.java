package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum SXItemHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return SXItemItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "SX-Item";
    }



}
