package pers.yufiria.craftorithm.hook;

import pers.yufiria.craftorithm.hook.recipe.resultProcessor.AdvancedEnchantmentsFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;

public enum AdvancedEnchantmentsHook implements PluginHook {

    INSTANCE;

    public static final String RULE_NAME = "ae_enchantments";

    @Override
    public String pluginName() {
        return "AdvancedEnchantments";
    }

    @Override
    public boolean hook() {
        boolean pluginEnabled = isPluginEnabled();
        if (pluginEnabled) {
            ResultProcessorManager.INSTANCE.registerFactory(AdvancedEnchantmentsFactory.INSTANCE);
        }
        return pluginEnabled;
    }

    @Override
    public void unhook() {
        ResultProcessorManager.INSTANCE.unregisterFactory(RULE_NAME);
    }

}
