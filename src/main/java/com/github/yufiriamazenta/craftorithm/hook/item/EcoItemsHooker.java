package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.EcoItemsItemProvider;

public enum EcoItemsHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public String pluginName() {
        return "EcoItems";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }

    @Override
    public ItemProvider itemProvider() {
        return EcoItemsItemProvider.INSTANCE;
    }

}
