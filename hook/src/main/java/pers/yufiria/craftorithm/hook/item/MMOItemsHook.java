package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
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
