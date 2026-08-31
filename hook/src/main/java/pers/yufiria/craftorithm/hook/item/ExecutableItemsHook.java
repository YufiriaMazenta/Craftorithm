package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum ExecutableItemsHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "ExecutableItems";
    }

    @Override
    public ItemProvider itemProvider() {
        return ExecutableItemsItemProvider.INSTANCE;
    }

}
