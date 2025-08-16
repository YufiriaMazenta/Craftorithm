package com.github.yufiriamazenta.craftorithm.hook.item;


import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.ExecutableItemsItemProvider;

public enum ExecutableItemsHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public String pluginName() {
        return "ExecutableItems";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }

    @Override
    public ItemProvider itemProvider() {
        return ExecutableItemsItemProvider.INSTANCE;
    }
}
