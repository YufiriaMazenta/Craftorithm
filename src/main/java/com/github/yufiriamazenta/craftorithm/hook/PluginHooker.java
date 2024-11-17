package com.github.yufiriamazenta.craftorithm.hook;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface PluginHooker extends BukkitLifeCycleTask {

    String pluginName();

    /**
     * 尝试挂钩插件,返回挂钩结果
     * @return 插件挂钩结果
     */
    boolean hook();

    @Override
    default void run(Plugin plugin, LifeCycle lifeCycle) {
        if (hook()) {
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        } else {
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, Map.of("<plugin>", pluginName()));
        }
    }

    default boolean hookByEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName());
    }

}
