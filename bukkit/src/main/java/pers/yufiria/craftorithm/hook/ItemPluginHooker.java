package pers.yufiria.craftorithm.hook;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.LifeCycle;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.Lang;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;

import java.util.Map;

public interface ItemPluginHooker extends PluginHooker {

    ItemProvider itemProvider();

    @Override
    default void run(Plugin plugin, LifeCycle lifeCycle) {
        if (hook()) {
            ItemManager.INSTANCE.regItemProvider(itemProvider());
            BukkitMsgSender.INSTANCE.info(Lang.pluginHookSuccess.value(), Map.of("<plugin>", pluginName()));
        } else {
            BukkitMsgSender.INSTANCE.info(Lang.pluginHookFailed.value(), Map.of("<plugin>", pluginName()));
        }
    }
}
