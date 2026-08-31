package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE))
public enum NeigeItemsHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return NeigeItemsItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "NeigeItems";
    }

}
