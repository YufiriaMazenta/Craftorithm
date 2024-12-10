package pers.yufiria.craftorithm.hook.impl;

import pers.yufiria.craftorithm.hook.ItemPluginHooker;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.NeigeItemsItemProvider;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ENABLE, priority = -1)//因为NeigeItems的物品可能包含其他插件的物品,所以需要让它先注册以保证他先识别
)
public enum NeigeItemsHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return NeigeItemsItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "NeigeItems";
    }

    @Override
    public boolean hook() {
        return hookByEnabled();
    }

}
