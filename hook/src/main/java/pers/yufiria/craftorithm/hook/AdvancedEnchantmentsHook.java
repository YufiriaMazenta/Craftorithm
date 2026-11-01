package pers.yufiria.craftorithm.hook;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.resultprocessor.AdvancedEnchantmentsFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessorManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

/**
 * AdvancedEnchantments 挂钩。
 * <p>
 * 必须挂在生命周期上：注册的高级附魔结果处理器只有在 hook() 被调用后才会进入
 * {@link ResultProcessorManager}，否则配方里的 ae_enchantments 组件会被当作未知组件跳过。
 */
@LifecycleTaskConfig(schedules = {
    @LifecycleSchedule(phase = LifecyclePhase.ACTIVE),
    @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
})
public enum AdvancedEnchantmentsHook implements PluginHook, LifecycleTask {

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

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        switch (lifeCycle) {
            case ACTIVE -> {
                if (hook()) {
                    LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
                }
            }
            case DISABLE -> unhook();
            default -> {
            }
        }
    }

}
