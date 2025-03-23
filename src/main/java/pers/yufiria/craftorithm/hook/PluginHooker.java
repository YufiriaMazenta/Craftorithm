package pers.yufiria.craftorithm.hook;

import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface PluginHooker {

    String pluginName();

    /**
     * 尝试挂钩插件,返回挂钩结果
     * @return 插件挂钩结果
     */
    boolean hook();

    default boolean isPluginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName());
    }

}
