package pers.yufiria.craftorithm.hook;

import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.util.LangUtils;
import crypticlib.lifecycle.LifeCycle;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface ItemPluginHooker extends PluginHooker {

    ItemProvider itemProvider();

    @Override
    default void run(Plugin plugin, LifeCycle lifeCycle) {
        if (hook()) {
            ItemManager.INSTANCE.regItemProvider(itemProvider());
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        } else {
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, Map.of("<plugin>", pluginName()));
        }
    }
}
