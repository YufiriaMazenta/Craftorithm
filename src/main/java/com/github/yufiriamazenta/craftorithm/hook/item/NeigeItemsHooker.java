package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.NeigeItemsItemProvider;

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
