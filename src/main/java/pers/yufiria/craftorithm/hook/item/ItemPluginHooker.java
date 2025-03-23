package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.PluginHooker;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.util.LangUtils;
import crypticlib.lifecycle.LifeCycle;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface ItemPluginHooker extends PluginHooker {

    ItemProvider itemProvider();

}
