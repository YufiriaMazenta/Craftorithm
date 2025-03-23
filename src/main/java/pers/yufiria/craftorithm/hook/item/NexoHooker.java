package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.NexoItemProvider;

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
