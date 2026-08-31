package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum CustomFishingHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "CustomFishing";
    }

    @Override
    public ItemProvider itemProvider() {
        return CustomFishingItemProvider.INSTANCE;
    }

}
