package pers.yufiria.craftorithm.hook.placeholder;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifecycleTaskSettings(rules = {
    @LifecycleRule(lifeCycle = Lifecycle.ACTIVE),
    @LifecycleRule(lifeCycle = Lifecycle.DISABLE)
})
public enum PlaceholderAPIHook implements PluginHook, LifecycleTask {

    INSTANCE;

    @Override
    public String pluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public boolean hook() {
        boolean pluginEnabled = isPluginEnabled();
        if (pluginEnabled) {
            CraftorithmPlaceholders.INSTANCE.register();
        }
        return pluginEnabled;
    }

    @Override
    public void unhook() {
        CraftorithmPlaceholders.INSTANCE.unregister();
    }

    @Override
    public void lifecycle(CrypticLibPlugin crypticLibPlugin, Lifecycle lifecycle) {
        if (lifecycle == Lifecycle.ACTIVE) {
            if (hook()) {
                LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
            }
        } else if (lifecycle == Lifecycle.DISABLE) {
            unhook();
        }
    }
}
