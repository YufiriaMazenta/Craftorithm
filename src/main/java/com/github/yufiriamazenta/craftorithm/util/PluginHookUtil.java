package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.config.Languages;
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
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Optional;

public class PluginHookUtil {

    private static Economy economy;
    private static PlayerPoints playerPoints;
    private static boolean economyLoaded, pointsLoaded, itemsAdderLoaded, oraxenLoaded, mythicLoaded, neigeItemsLoaded;

    public static void hookPlugins() {
        hookVault();
        hookPlayerPoints();
        hookItemsAdder();
        hookOraxen();
        hookMythicMobs();
        hookNeigeItems();
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
        if (itemsAdderLoaded)
            LangUtil.info(Languages.LOAD_ITEMS_ADDER_HOOK.value());
        else
            LangUtil.info(Languages.LOAD_ITEMS_ADDER_NOT_EXIST.value());
    }

    public static boolean isItemsAdderLoaded() {
        return itemsAdderLoaded;
    }

    public static ItemStack getItemsAdderItem(String itemStr) {
        if (!itemsAdderLoaded)
            throw new UnsupportedOperationException("Can not found ItemsAdder plugin");
        CustomStack customStack = CustomStack.getInstance(itemStr);
        if (customStack == null) {
            throw new IllegalArgumentException("Can not found item " + itemStr + " from ItemsAdder");
        }
        return customStack.getItemStack();
    }

    public static String getItemsAdderName(ItemStack itemStack) {
        if (!itemsAdderLoaded)
            return null;
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null)
            return null;
        return customStack.getId();
    }

    private static void hookOraxen() {
        oraxenLoaded = Bukkit.getPluginManager().isPluginEnabled("Oraxen");
        if (oraxenLoaded)
            LangUtil.info(Languages.LOAD_ORAXEN_HOOK.value());
        else
            LangUtil.info(Languages.LOAD_ORAXEN_NOT_EXIST.value());
    }

    public static ItemStack getOraxenItem(String itemStr) {
        if (!oraxenLoaded)
            throw new UnsupportedOperationException("Can not found Oraxen plugin");
        if (!OraxenItems.exists(itemStr)) {
            throw new IllegalArgumentException("Can not found item " + itemStr + " from Oraxen");
        }
        return OraxenItems.getItemById(itemStr).build();
    }

    public static String getOraxenName(ItemStack itemStack) {
        if (!oraxenLoaded)
            return null;
        if (!OraxenItems.exists(itemStack))
            return null;
        return OraxenItems.getIdByItem(itemStack);
    }

    private static void hookMythicMobs() {
        mythicLoaded = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        if (mythicLoaded)
            LangUtil.info(Languages.LOAD_MYTHIC_MOBS_HOOK.value());
        else
            LangUtil.info(Languages.LOAD_MYTHIC_MOBS_NOT_EXIST.value());
    }

    public static ItemStack getMythicMobsItem(String itemStr) {
        if (!mythicLoaded)
            throw new UnsupportedOperationException("Can not found MythicMobs plugin");
        ItemExecutor executor = MythicBukkit.inst().getItemManager();
        Optional<MythicItem> itemOptional = executor.getItem(itemStr);
        if (!itemOptional.isPresent()) {
            throw new IllegalArgumentException("Can not found item " + itemStr + " from MythicMobs");
        }
        MythicItem mythicItem = itemOptional.get();
        int amount = mythicItem.getAmount();
        return BukkitAdapter.adapt(itemOptional.get().generateItemStack(amount));
    }

    public static String getMythicMobsName(ItemStack itemStack) {
        if (!mythicLoaded)
            return null;
        ItemExecutor itemExecutor = MythicBukkit.inst().getItemManager();
        if (!itemExecutor.isMythicItem(itemStack))
            return null;
        return itemExecutor.getMythicTypeFromItem(itemStack);
    }

    public ItemExecutor getMythicMobsItemExecutor() {
        if (!mythicLoaded)
            throw new UnsupportedOperationException("Can not found MythicMobs plugin");
        return MythicBukkit.inst().getItemManager();
    }

    private static void hookNeigeItems() {
        neigeItemsLoaded = Bukkit.getPluginManager().isPluginEnabled("NeigeItems");
        if (neigeItemsLoaded)
            LangUtil.info(Languages.LOAD_NEIGE_ITEMS_HOOK.value());
        else
            LangUtil.info(Languages.LOAD_NEIGE_ITEMS_NOT_EXIST.value());
    }

    public static ItemStack getNiItem(String itemName) {
        if (!neigeItemsLoaded)
            throw new UnsupportedOperationException("Can not found NeigeItems plugin");
        if (!ItemManager.INSTANCE.hasItem(itemName))
            return null;
        return ItemManager.INSTANCE.getItemStack(itemName);
    }

    public static String getNiName(ItemStack item) {
        if (!neigeItemsLoaded)
            throw new UnsupportedOperationException("Can not found NeigeItems plugin");
        ItemInfo niItem = ItemManager.INSTANCE.isNiItem(item);
        if (niItem == null) {
            return null;
        }
        return niItem.getId();
    }

}
