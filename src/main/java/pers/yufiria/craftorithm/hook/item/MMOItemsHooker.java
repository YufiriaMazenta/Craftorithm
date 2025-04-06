package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.MMOItemsItemProvider;

public enum MMOItemsHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return MMOItemsItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "MMOItems";
    }

    @Override
    public boolean hook() {
        return isPluginEnabled();
    }
}
