package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.item.impl.ItemsAdderItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.MythicMobsItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.NeigeItemsItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.OraxenItemProvider;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import io.th0rgal.oraxen.api.OraxenItems;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import pers.neige.neigeitems.item.ItemInfo;

import java.util.Optional;

public class PluginHookUtil {

    private static Economy economy;
    private static PlayerPoints playerPoints;
    private static boolean economyLoaded, pointsLoaded, itemsAdderLoaded, oraxenLoaded, mythicLoaded, neigeItemsLoaded;

    public static void hookPlugins() {
        hookVault();
        hookPlayerPoints();
        hookNeigeItems();
        hookItemsAdder();
        hookOraxen();
        hookMythicMobs();
    }

    private static void hookVault() {
        economyLoaded = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (!economyLoaded) {
            LangUtil.info(Languages.LOAD_VAULT_NOT_EXIST.value());
            return;
        }
        RegisteredServiceProvider<Economy> vaultRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (vaultRsp == null) {
            economyLoaded = false;
            LangUtil.info(Languages.LOAD_VAULT_NOT_EXIST.value());
            return;
        }

        LangUtil.info(Languages.LOAD_VAULT_HOOK.value());
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
            LangUtil.info(Languages.LOAD_POINTS_HOOK.value());
        } else
            LangUtil.info(Languages.LOAD_POINTS_NOT_EXIST.value());
    }

    public static boolean isPlayerPointsLoaded() { return pointsLoaded; }

    public static PlayerPoints getPlayerPoints() {
        return playerPoints;
    }

    private static void hookItemsAdder() {
        itemsAdderLoaded = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
        if (itemsAdderLoaded) {
            LangUtil.info(Languages.LOAD_ITEMS_ADDER_HOOK.value());
            ItemManager.INSTANCE.regItemProvider(ItemsAdderItemProvider.INSTANCE);
        }
        else
            LangUtil.info(Languages.LOAD_ITEMS_ADDER_NOT_EXIST.value());
    }

    private static void hookOraxen() {
        oraxenLoaded = Bukkit.getPluginManager().isPluginEnabled("Oraxen");
        if (oraxenLoaded) {
            LangUtil.info(Languages.LOAD_ORAXEN_HOOK.value());
            ItemManager.INSTANCE.regItemProvider(OraxenItemProvider.INSTANCE);
        }
        else
            LangUtil.info(Languages.LOAD_ORAXEN_NOT_EXIST.value());
    }

    private static void hookMythicMobs() {
        mythicLoaded = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        if (mythicLoaded) {
            ItemManager.INSTANCE.regItemProvider(MythicMobsItemProvider.INSTANCE);
            LangUtil.info(Languages.LOAD_MYTHIC_MOBS_HOOK.value());
        }
        else
            LangUtil.info(Languages.LOAD_MYTHIC_MOBS_NOT_EXIST.value());
    }

    private static void hookNeigeItems() {
        neigeItemsLoaded = Bukkit.getPluginManager().isPluginEnabled("NeigeItems");
        if (neigeItemsLoaded) {
            ItemManager.INSTANCE.regItemProvider(NeigeItemsItemProvider.INSTANCE);
            LangUtil.info(Languages.LOAD_NEIGE_ITEMS_HOOK.value());
        }
        else
            LangUtil.info(Languages.LOAD_NEIGE_ITEMS_NOT_EXIST.value());
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

}
