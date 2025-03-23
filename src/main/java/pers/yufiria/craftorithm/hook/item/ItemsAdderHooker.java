package pers.yufiria.craftorithm.hook.item;

import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.ItemsAdderItemProvider;
import pers.yufiria.craftorithm.hook.listener.ItemsAdderHandler;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.Bukkit;

public enum ItemsAdderHooker implements ItemPluginHooker {

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
