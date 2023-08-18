package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.bstat.Metrics;
import com.github.yufiriamazenta.craftorithm.config.ConfigUpdater;
import com.github.yufiriamazenta.craftorithm.item.manager.DefItemManager;
import com.github.yufiriamazenta.craftorithm.item.manager.IItemManager;
import com.github.yufiriamazenta.craftorithm.listener.*;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuDispatcher;
import com.github.yufiriamazenta.craftorithm.recipe.DefRecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.manager.IRecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import com.github.yufiriamazenta.craftorithm.util.UpdateUtil;
import crypticlib.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;

public final class Craftorithm extends BukkitPlugin implements Listener {

    private static Craftorithm INSTANCE;
    private IRecipeManager recipeManager;
    private IItemManager itemManager;

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        saveDefaultConfig();
        ConfigUpdater.INSTANCE.updateConfig();

        itemManager = DefItemManager.INSTANCE;
        recipeManager = DefRecipeManager.INSTANCE;
        regListeners();
        PluginHookUtil.hookPlugins();
        initArcenciel();
        loadBStat();
        
        LangUtil.info("load.finish");
        UpdateUtil.checkUpdate(Bukkit.getConsoleSender());
    }

    @Override
    public void disable() {

    }

    private void loadBStat() {
        Metrics metrics = new Metrics(this, 17821);
    }

    private void regListeners() {
        Bukkit.getPluginManager().registerEvents(CraftHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(RecipeUnlockHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(BukkitMenuDispatcher.INSTANCE, this);
        if (getVanillaVersion() >= 14)
            Bukkit.getPluginManager().registerEvents(SmithingHandler.INSTANCE, this);
        if (getVanillaVersion() >= 17)
            Bukkit.getPluginManager().registerEvents(FurnaceSmeltHandler.INSTANCE, this);
    }

    private void initArcenciel() {
        ArcencielDispatcher.INSTANCE.loadFuncFile();
    }

    public static Craftorithm getInstance() {
        return INSTANCE;
    }

    public IRecipeManager getRecipeManager() {
        return recipeManager;
    }

    public IItemManager getItemManager() {
        return itemManager;
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
