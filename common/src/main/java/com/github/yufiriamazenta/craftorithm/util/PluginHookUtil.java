package com.github.yufiriamazenta.craftorithm.util;

import crypticlib.util.MsgUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PluginHookUtil {

    private static Economy economy;
    private static PlayerPoints playerPoints;
    private static boolean economyLoaded, pointsLoaded;

    private static boolean hookVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> vaultRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (vaultRsp == null) {
            return false;
        }
        economy = vaultRsp.getProvider();
        return true;
    }

    private static boolean hookPlayerPoints() {
        playerPoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
        return playerPoints != null;
    }


    public static Economy getEconomy() {
        return economy;
    }

    public static PlayerPoints getPlayerPoints() {
        return playerPoints;
    }

    public static boolean isEconomyLoaded() { return economyLoaded; }

    public static boolean isPlayerPointsLoaded() { return pointsLoaded; }

    public static void hookPlugins() {
        economyLoaded = hookVault();
        String messageKey = "load.vault_success";
        if (!economyLoaded)
            messageKey = "load.vault_failed";
        MsgUtil.info(messageKey);
        pointsLoaded = hookPlayerPoints();
        messageKey = "load.points_success";
        if (!pointsLoaded)
            messageKey = "load.points_failed";
        MsgUtil.info(messageKey);
    }

}
