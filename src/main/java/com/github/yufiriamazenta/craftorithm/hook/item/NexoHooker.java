package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.NexoItemProvider;

public enum NexoHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return NexoItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "Nexo";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }

}
