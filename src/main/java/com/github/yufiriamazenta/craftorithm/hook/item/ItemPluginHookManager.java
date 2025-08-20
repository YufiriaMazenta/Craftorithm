package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.impl.CraftorithmItemProvider;
import com.github.yufiriamazenta.craftorithm.util.LangUtils;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进行物品插件挂钩的类
 */
@AutoTask(rules = {
    @TaskRule(lifeCycle = LifeCycle.ACTIVE),
    @TaskRule(lifeCycle = LifeCycle.RELOAD)
})
public enum ItemPluginHookManager implements BukkitLifeCycleTask {

    INSTANCE;
    private final Map<String, ItemPluginHooker> itemPluginHookerMap = new ConcurrentHashMap<>();

    ItemPluginHookManager() {
        addItemPluginHooker(AzureFlowHooker.INSTANCE);
        addItemPluginHooker(EcoItemsHooker.INSTANCE);
        addItemPluginHooker(ExecutableItemsHooker.INSTANCE);
        addItemPluginHooker(ItemsAdderHooker.INSTANCE);
        addItemPluginHooker(MMOItemsHooker.INSTANCE);
        addItemPluginHooker(MythicMobs5Hooker.INSTANCE);
        addItemPluginHooker(NeigeItemsHooker.INSTANCE);
        addItemPluginHooker(NexoHooker.INSTANCE);
        addItemPluginHooker(OraxenHooker.INSTANCE);
        addItemPluginHooker(CraftEngineHooker.INSTANCE);
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        ItemManager.INSTANCE.resetItemProviders();
        for (String hookPluginName : PluginConfigs.ITEM_PLUGIN_HOOK_PRIORITY.value()) {
            ItemPluginHooker itemPluginHooker = getItemPluginHooker(hookPluginName);
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

    public void addItemPluginHooker(ItemPluginHooker hooker) {
        itemPluginHookerMap.put(hooker.pluginName(), hooker);
    }

    public ItemPluginHooker getItemPluginHooker(String pluginName) {
        return itemPluginHookerMap.get(pluginName);
    }

}
