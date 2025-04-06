package pers.yufiria.craftorithm.hook;

import org.bukkit.Bukkit;

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
