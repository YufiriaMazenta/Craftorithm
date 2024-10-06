package pers.yufiria.craftorithm.hook;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.Lang;

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
            BukkitMsgSender.INSTANCE.info(Lang.pluginHookSuccess.value(), Map.of("<plugin>", pluginName()));
        } else {
            BukkitMsgSender.INSTANCE.info(Lang.pluginHookFailed.value(), Map.of("<plugin>", pluginName()));
        }
    }

    default boolean hookByEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName());
    }

}
