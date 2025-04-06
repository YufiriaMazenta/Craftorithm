package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.ExecutableItemsItemProvider;

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
