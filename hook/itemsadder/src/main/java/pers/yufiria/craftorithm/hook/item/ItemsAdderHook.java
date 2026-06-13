package pers.yufiria.craftorithm.hook.item;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.Bukkit;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.listener.ItemsAdderHandler;
import pers.yufiria.craftorithm.item.ItemProvider;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE)
})
public enum ItemsAdderHook implements ItemPluginHook {

    INSTANCE;

    @Override
    public String pluginName() {
        return "ItemsAdder";
    }

    @Override
    public boolean hook() {
        boolean hooked = isPluginEnabled();
        if (hooked) {
            BukkitMsgSender.INSTANCE.debug("[Craftorithm] Registering ItemsAdder Handler");
            Bukkit.getPluginManager().registerEvents(ItemsAdderHandler.INSTANCE, Craftorithm.instance());
        }
        return hooked;
    }

    @Override
    public ItemProvider itemProvider() {
        return ItemsAdderItemProvider.INSTANCE;
    }

}
