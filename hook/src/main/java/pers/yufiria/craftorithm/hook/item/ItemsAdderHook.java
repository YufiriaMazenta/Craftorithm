package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum ItemsAdderHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "ItemsAdder";
    }

    @Override
    public ItemProvider itemProvider() {
        return ItemsAdderItemProvider.INSTANCE;
    }

}
