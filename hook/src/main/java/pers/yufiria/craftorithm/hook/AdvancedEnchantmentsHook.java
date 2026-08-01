package pers.yufiria.craftorithm.hook;

import pers.yufiria.craftorithm.hook.recipe.copyComponents.AdvancedEnchantmentsEnchantments;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;

public enum AdvancedEnchantmentsHook implements PluginHook {

    INSTANCE;

    public static final String RULE_NAME = "advancedenchantments_enchantments";

    @Override
    public String pluginName() {
        return "AdvancedEnchantments";
    }

    @Override
    public boolean hook() {
        boolean pluginEnabled = isPluginEnabled();
        if (pluginEnabled) {
            CopyComponentsManager.INSTANCE.registerCopyNbtRuleCreator(
                RULE_NAME,
                arg -> AdvancedEnchantmentsEnchantments.INSTANCE
            );
        }
        return pluginEnabled;
    }


    @Override
    public void unhook() {
        CopyComponentsManager.INSTANCE.unregisterCopyNbtRule(RULE_NAME);
    }

}
