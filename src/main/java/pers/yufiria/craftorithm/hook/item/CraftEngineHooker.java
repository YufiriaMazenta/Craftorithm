package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.CraftEngineItemProvider;

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
