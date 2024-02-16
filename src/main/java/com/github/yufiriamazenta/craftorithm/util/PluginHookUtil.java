package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.impl.*;
import com.github.yufiriamazenta.craftorithm.listener.OraxenHandler;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PluginHookUtil {

    private static Economy economy;
    private static PlayerPoints playerPoints;
    private static boolean economyLoaded, pointsLoaded, itemsAdderLoaded, oraxenLoaded, mythicLoaded, neigeItemsLoaded, mmoitemsLoaded;

    public static void hookPlugins() {
        hookVault();
        hookPlayerPoints();
        hookNeigeItems();
        hookItemsAdder();
        hookOraxen();
        hookMMOItems();
        hookMythicMobs();
    }

    private static void hookVault() {
        economyLoaded = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (!economyLoaded) {
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "Vault"));
            return;
        }
        RegisteredServiceProvider<Economy> vaultRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (vaultRsp == null) {
            economyLoaded = false;
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "Vault"));
            return;
        }

        LangUtil.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, CollectionsUtil.newStringHashMap("<plugin>", "Vault"));
        economyLoaded = true;
        economy = vaultRsp.getProvider();
    }

    public static boolean isEconomyLoaded() { return economyLoaded; }

    public static Economy getEconomy() {
        return economy;
    }

    private static void hookPlayerPoints() {
        pointsLoaded = Bukkit.getPluginManager().isPluginEnabled("PlayerPoints");
        if (pointsLoaded) {
            playerPoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, CollectionsUtil.newStringHashMap("<plugin>", "PlayerPoints"));
        } else
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "PlayerPoints"));
    }

    public static boolean isPlayerPointsLoaded() { return pointsLoaded; }

    public static PlayerPoints getPlayerPoints() {
        return playerPoints;
    }

    private static void hookItemsAdder() {
        itemsAdderLoaded = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
        if (itemsAdderLoaded) {
            ItemManager.INSTANCE.regItemProvider(ItemsAdderItemProvider.INSTANCE);
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, CollectionsUtil.newStringHashMap("<plugin>", "ItemsAdder"));
        }
        else
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "ItemsAdder"));
    }

    private static void hookOraxen() {
        oraxenLoaded = Bukkit.getPluginManager().isPluginEnabled("Oraxen");
        if (oraxenLoaded) {
            ItemManager.INSTANCE.regItemProvider(OraxenItemProvider.INSTANCE);
            Bukkit.getPluginManager().registerEvents(OraxenHandler.INSTANCE, Craftorithm.instance());
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, CollectionsUtil.newStringHashMap("<plugin>", "Oraxen"));
        }
        else
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "Oraxen"));
    }

    private static void hookMythicMobs() {
        mythicLoaded = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        if (mythicLoaded) {
            ItemManager.INSTANCE.regItemProvider(MythicMobsItemProvider.INSTANCE);
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, CollectionsUtil.newStringHashMap("<plugin>", "MythicMobs"));
        }
        else
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "MythicMobs"));
    }

    private static void hookNeigeItems() {
        neigeItemsLoaded = Bukkit.getPluginManager().isPluginEnabled("NeigeItems");
        if (neigeItemsLoaded) {
            ItemManager.INSTANCE.regItemProvider(NeigeItemsItemProvider.INSTANCE);
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, CollectionsUtil.newStringHashMap("<plugin>", "NeigeItems"));
        }
        else
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "NeigeItems"));
    }

    private static void hookMMOItems() {
        mmoitemsLoaded = Bukkit.getPluginManager().isPluginEnabled("MMOItems");
        if (mmoitemsLoaded) {
            ItemManager.INSTANCE.regItemProvider(MMOItemsItemProvider.INSTANCE);
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, CollectionsUtil.newStringHashMap("<plugin>", "MMOItems"));
        }
        else
            LangUtil.info(Languages.LOAD_HOOK_PLUGIN_NOT_EXIST, CollectionsUtil.newStringHashMap("<plugin>", "MMOItems"));
    }

    public static boolean isItemsAdderLoaded() {
        return itemsAdderLoaded;
    }

    public static boolean isOraxenLoaded() {
        return oraxenLoaded;
    }

    public static boolean isMythicMobsLoaded() {
        return mythicLoaded;
    }

    public static boolean isNeigeItemsLoaded() {
        return neigeItemsLoaded;
    }

    public static boolean isMmoitemsLoaded() {
        return mmoitemsLoaded;
    }

}
