package pers.yufiria.craftorithm.hook.item;

import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.CraftorithmItemProvider;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进行物品插件挂钩的管理器
 */
@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.ACTIVE),
    @TaskRule(lifeCycle = LifeCycle.RELOAD)
})
public enum ItemPluginHookManager implements BukkitLifeCycleTask {

    INSTANCE;
    private final Map<String, ItemPluginHook> itemPluginHookMap = new ConcurrentHashMap<>();

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        for (ItemPluginHook itemPluginHook : itemPluginHookMap.values()) {
            itemPluginHook.unhook();
        }
        ItemManager.INSTANCE.resetItemProviders();
        for (String hookPluginName : PluginConfigs.ITEM_PLUGIN_HOOK_PRIORITY.value()) {
            ItemPluginHook itemPluginHooker = getItemPluginHook(hookPluginName);
            if (itemPluginHooker == null) {
                IOHelper.info("&eUnknown item plugin '" + hookPluginName + "'");
                continue;
            }
            if (itemPluginHooker.hook()) {
                ItemManager.INSTANCE.regItemProvider(itemPluginHooker.itemProvider());
                LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", hookPluginName));
            }
        }
        ItemManager.INSTANCE.regItemProvider(CraftorithmItemProvider.INSTANCE);
    }

    public void addItemPluginHook(ItemPluginHook hooker) {
        itemPluginHookMap.put(hooker.pluginName(), hooker);
    }

    public ItemPluginHook getItemPluginHook(String pluginName) {
        return itemPluginHookMap.get(pluginName);
    }

}
