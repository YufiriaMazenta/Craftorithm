package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum MythicMobsHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "MythicMobs";
    }

    @Override
    public ItemProvider itemProvider() {
        return MythicMobsItemProvider.INSTANCE;
    }

}
