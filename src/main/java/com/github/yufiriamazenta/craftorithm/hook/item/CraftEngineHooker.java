package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftEngineItemProvider;

public enum CraftEngineHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return CraftEngineItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "CraftEngine";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }
}
