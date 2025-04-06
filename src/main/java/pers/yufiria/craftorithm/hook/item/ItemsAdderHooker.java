package pers.yufiria.craftorithm.hook.item;

import crypticlib.chat.BukkitMsgSender;
import org.bukkit.Bukkit;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.listener.ItemsAdderHandler;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.impl.ItemsAdderItemProvider;

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
