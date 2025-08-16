package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.AzureFlowItemProvider;

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