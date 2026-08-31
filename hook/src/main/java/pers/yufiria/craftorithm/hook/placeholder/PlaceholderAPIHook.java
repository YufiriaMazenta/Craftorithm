package pers.yufiria.craftorithm.hook.placeholder;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifecycleTaskConfig(schedules = {
    @LifecycleSchedule(phase = LifecyclePhase.ACTIVE),
    @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
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
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecycle) {
        if (lifecycle == LifecyclePhase.ACTIVE) {
            if (hook()) {
                LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
            }
        } else if (lifecycle == LifecyclePhase.DISABLE) {
            unhook();
        }
    }
}
