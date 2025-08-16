package com.github.yufiriamazenta.craftorithm.hook.item;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.hook.listener.ItemsAdderHandler;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.ItemsAdderItemProvider;
import crypticlib.chat.BukkitMsgSender;
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
