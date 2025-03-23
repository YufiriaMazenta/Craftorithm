package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.NeigeItemsItemProvider;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;

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
        return isPluginEnabled();
    }

}
