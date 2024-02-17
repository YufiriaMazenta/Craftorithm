package com.github.yufiria.craftorithm;

import com.github.yufiria.craftorithm.util.UpdateUtil;
import crypticlib.BukkitPlugin;
import crypticlib.chat.MessageSender;
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
        MessageSender.info("&aCraftorithm Enabled");
    }

    @Override
    public void disable() {
    }

    private void loadBStat() {
//        if (!PluginConfigs.BSTATS.value())
//            return;
//        Metrics metrics = new Metrics(this, 17821);
//        metrics.addCustomChart(new Metrics.SingleLineChart("recipes", () -> RecipeManager.INSTANCE.getRecipeGroups().size()));
    }

    private void regListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void initArcenciel() {
//        ArcencielDispatcher.INSTANCE.loadFuncFile();
    }

    public static Craftorithm instance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateUtil.pullUpdateCheckRequest(event.getPlayer());
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
//        if (!PluginHookUtil.isItemsAdderLoaded()) {
//            RecipeManager.INSTANCE.reloadRecipeManager();
//            return;
//        }
//        Bukkit.getPluginManager().registerEvents(ItemsAdderHandler.INSTANCE, this);
//        UpdateUtil.pullUpdateCheckRequest(Bukkit.getConsoleSender());
    }
}
