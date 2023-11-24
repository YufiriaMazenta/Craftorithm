package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.bstat.Metrics;
import com.github.yufiriamazenta.craftorithm.config.ConfigUpdater;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.listener.FurnaceSmeltHandler;
import com.github.yufiriamazenta.craftorithm.listener.ItemsAdderHandler;
import com.github.yufiriamazenta.craftorithm.listener.SmithingHandler;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import com.github.yufiriamazenta.craftorithm.util.UpdateUtil;
import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
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

        ItemManager.reloadItemManager();
        regListeners();
        initArcenciel();
        loadBStat();
        
        LangUtil.info("load.finish");
        UpdateUtil.pullUpdateCheckRequest(Bukkit.getConsoleSender());
    }

    @Override
    public void disable() {
        RecipeManager.resetRecipes();
    }

    private void loadBStat() {
        if (!getConfig().getBoolean("bstats", true))
            return;
        Metrics metrics = new Metrics(this, 17821);
        metrics.addCustomChart(new Metrics.SingleLineChart("recipes", () -> RecipeManager.recipeConfigWrapperMap().keySet().size()));
    }

    private void regListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
        if (CrypticLib.minecraftVersion() >= 11400)
            Bukkit.getPluginManager().registerEvents(SmithingHandler.INSTANCE, this);
        if (CrypticLib.minecraftVersion() >= 11700)
            Bukkit.getPluginManager().registerEvents(FurnaceSmeltHandler.INSTANCE, this);
    }

    private void initArcenciel() {
        ArcencielDispatcher.INSTANCE.loadFuncFile();
    }

    public static Craftorithm instance() {
        return INSTANCE;
    }

    public static CraftorithmAPI api() {
        return CraftorithmAPI.INSTANCE;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateUtil.pullUpdateCheckRequest(event.getPlayer());
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        PluginHookUtil.hookPlugins();
        if (!PluginHookUtil.isItemsAdderLoaded()) {
            RecipeManager.reloadRecipeManager();
            return;
        }
        Bukkit.getPluginManager().registerEvents(ItemsAdderHandler.INSTANCE, this);
    }
}
