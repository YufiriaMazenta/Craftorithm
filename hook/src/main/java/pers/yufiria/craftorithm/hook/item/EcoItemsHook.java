package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
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
