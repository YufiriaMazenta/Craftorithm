package pers.yufiria.craftorithm.test;

import pers.yufiria.craftorithm.hook.item.ItemPluginHook;
import pers.yufiria.craftorithm.item.ItemProvider;

public enum TestHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return TestItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "Test";
    }

    @Override
    public boolean hook() {
        return true;
    }

}
