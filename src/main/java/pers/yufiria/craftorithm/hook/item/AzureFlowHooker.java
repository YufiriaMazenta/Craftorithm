package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.AzureFlowItemProvider;

public enum AzureFlowHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return AzureFlowItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "AzureFlow";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }

}