package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.bstat.Metrics;
import com.github.yufiriamazenta.craftorithm.config.Config;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import com.github.yufiriamazenta.craftorithm.util.UpdateUtil;
import crypticlib.BukkitPlugin;
import crypticlib.util.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;

public final class Craftorithm extends BukkitPlugin implements Listener {

    private static Craftorithm INSTANCE;
    private Metrics metrics;

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        saveDefaultConfig();
        reloadConfig();
        loadBStat();

        PluginHookUtil.hookPlugins();

        MsgUtil.info("load.finish");
        UpdateUtil.checkUpdate(Bukkit.getConsoleSender());
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        for (Config.BooleanConfig booleanConfigValue : Config.BooleanConfig.values()) {
            booleanConfigValue.reload();
        }
    }

    @Override
    public void disable() {

    }

    private void loadBStat() {
        if (!Config.BooleanConfig.B_STATS.value())
            return;
        metrics = new Metrics(this, 17821);
    }

    public static Craftorithm getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateUtil.checkUpdate(event.getPlayer());
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        //todo
    }

    public Metrics getMetrics() {
        return metrics;
    }

}
