package com.github.yufiriamazenta.craftorithm.hook;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
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
