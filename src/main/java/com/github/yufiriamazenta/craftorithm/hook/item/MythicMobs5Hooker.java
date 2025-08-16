package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.MythicMobsItemProvider;

public enum MythicMobs5Hooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public String pluginName() {
        return "MythicMobs";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }

    @Override
    public ItemProvider itemProvider() {
        return MythicMobsItemProvider.INSTANCE;
    }

}
