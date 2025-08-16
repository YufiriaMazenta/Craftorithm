package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.OraxenItemProvider;

public enum OraxenHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public String pluginName() {
        return "Oraxen";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }

    @Override
    public ItemProvider itemProvider() {
        return OraxenItemProvider.INSTANCE;
    }

}
