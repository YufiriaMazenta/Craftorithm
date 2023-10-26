package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.bstat.Metrics;
import com.github.yufiriamazenta.craftorithm.config.ConfigUpdater;
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

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        saveDefaultConfig();
        ConfigUpdater.INSTANCE.updateConfig();

        PluginHookUtil.hookPlugins();
        loadBStat();
        
        MsgUtil.info("load.finish");
        UpdateUtil.checkUpdate(Bukkit.getConsoleSender());
    }

    @Override
    public void disable() {

    }

    private void loadBStat() {
        Metrics metrics = new Metrics(this, 17821);
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

}
