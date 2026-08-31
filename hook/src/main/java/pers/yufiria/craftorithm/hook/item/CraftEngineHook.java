package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum CraftEngineHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return CraftEngineItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "CraftEngine";
    }



}
