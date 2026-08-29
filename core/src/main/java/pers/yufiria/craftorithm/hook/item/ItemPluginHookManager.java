package pers.yufiria.craftorithm.hook.item;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进行物品插件挂钩的管理器
 */
@LifecycleTaskSettings(rules = {
    @LifecycleRule(lifeCycle = Lifecycle.ACTIVE),
    @LifecycleRule(lifeCycle = Lifecycle.RELOAD)
})
public enum ItemPluginHookManager implements LifecycleTask {

    INSTANCE;
    private final Map<String, ItemPluginHook> itemPluginHookMap = new ConcurrentHashMap<>();

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        for (ItemPluginHook itemPluginHook : itemPluginHookMap.values()) {
            itemPluginHook.unhook();
        }
        ItemManager.INSTANCE.resetItemProviders();
        for (String hookPluginName : PluginConfigs.ITEM_PLUGIN_HOOK_PRIORITY.value()) {
            ItemPluginHook itemPluginHooker = getItemPluginHook(hookPluginName);
            if (itemPluginHooker == null) {
                CrypticLib.info("&eUnknown item plugin '" + hookPluginName + "'");
                continue;
            }
            if (itemPluginHooker.hook()) {
                ItemManager.INSTANCE.regItemProvider(itemPluginHooker.itemProvider());
                LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", hookPluginName));
            }
        }
    }

    public void addItemPluginHook(ItemPluginHook hooker) {
        itemPluginHookMap.put(hooker.pluginName(), hooker);
    }

    public ItemPluginHook getItemPluginHook(String pluginName) {
        return itemPluginHookMap.get(pluginName);
    }

}
